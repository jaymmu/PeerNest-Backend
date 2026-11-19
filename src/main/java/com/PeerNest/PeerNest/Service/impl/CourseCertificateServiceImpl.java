
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.CertificateVerificationResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.CourseCertificateService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseCertificateServiceImpl
        implements CourseCertificateService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository progressRepository;
    private final CourseCertificateRepository certificateRepository;

    @Value("${app.certificate-verification-url:http://localhost:8081/api/certificates/verify/}")
    private String verificationUrl;

    @Override
    @Transactional
    public byte[] downloadCertificate(
            Long courseId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        boolean enrolled =
                enrollmentRepository.existsByStudentIdAndCourseId(
                        student.getId(),
                        courseId
                );

        if (!enrolled) {
            throw new RuntimeException(
                    "You are not enrolled in this course"
            );
        }

        // Calculate total lectures.
        int totalLectures = course.getSections()
                .stream()
                .mapToInt(section ->
                        section.getLectures().size())
                .sum();

        // Calculate completed lectures.
        long completedLectures =
                progressRepository
                        .countByStudentIdAndLectureSectionCourseIdAndCompleted(
                                student.getId(),
                                courseId,
                                true
                        );

        if (totalLectures == 0 ||
                completedLectures < totalLectures) {

            throw new RuntimeException(
                    "Complete all lectures before downloading your certificate"
            );
        }

        // Reuse existing certificate.
        CourseCertificate certificate =
                certificateRepository
                        .findByStudentIdAndCourseId(
                                student.getId(),
                                courseId
                        )
                        .orElseGet(() -> {

                            CourseCertificate newCertificate =
                                    CourseCertificate.builder()
                                            .certificateNumber(
                                                    "PN-" +
                                                            UUID.randomUUID()
                                                                    .toString()
                                                                    .substring(0, 12)
                                                                    .toUpperCase()
                                            )
                                            .issueDate(LocalDate.now())
                                            .student(student)
                                            .course(course)
                                            .build();

                            return certificateRepository.save(
                                    newCertificate
                            );
                        });

        return generatePdf(certificate);
    }

    // Public certificate verification.
    @Override
    @Transactional(readOnly = true)
    public CertificateVerificationResponse verifyCertificate(
            String certificateNumber) {

        return certificateRepository
                .findByCertificateNumber(certificateNumber)
                .map(certificate ->
                        new CertificateVerificationResponse(
                                true,
                                certificate.getCertificateNumber(),
                                certificate.getStudent().getName(),
                                certificate.getCourse().getTitle(),
                                certificate.getIssueDate(),
                                "This certificate is valid and was issued by PeerNest."
                        )
                )
                .orElseGet(() ->
                        new CertificateVerificationResponse(
                                false,
                                certificateNumber,
                                null,
                                null,
                                null,
                                "Invalid certificate. No matching record was found."
                        )
                );
    }

    // Generate certificate PDF with QR code.
    private byte[] generatePdf(
            CourseCertificate certificate) {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            PDRectangle landscape =
                    new PDRectangle(842, 595);

            PDPage page = new PDPage(landscape);
            document.addPage(page);

            PDType1Font regular = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

            PDType1Font bold = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                // Outer border.
                content.setLineWidth(3);
                content.addRect(25, 25, 792, 545);
                content.stroke();

                // Inner border.
                content.setLineWidth(1);
                content.addRect(35, 35, 772, 525);
                content.stroke();

                drawCenteredText(
                        content, bold, 30,
                        "PeerNest", 842, 500
                );

                drawCenteredText(
                        content, bold, 32,
                        "CERTIFICATE OF COMPLETION", 842, 420
                );

                drawCenteredText(
                        content, regular, 17,
                        "This certificate is proudly presented to",
                        842, 355
                );

                drawCenteredText(
                        content, bold, 28,
                        certificate.getStudent().getName(),
                        842, 305
                );

                drawCenteredText(
                        content, regular, 17,
                        "for successfully completing the course",
                        842, 260
                );

                drawCenteredText(
                        content, bold, 24,
                        certificate.getCourse().getTitle(),
                        842, 215
                );

                String date = certificate.getIssueDate()
                        .format(DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy"
                        ));

                drawCenteredText(
                        content, regular, 13,
                        "Completion Date: " + date,
                        842, 145
                );

                drawCenteredText(
                        content, regular, 11,
                        "Certificate ID: " +
                                certificate.getCertificateNumber(),
                        842, 105
                );

                drawCenteredText(
                        content, bold, 13,
                        "PeerNest Learning Platform",
                        842, 65
                );

                // Generate QR code.
                byte[] qrBytes = generateQrCode(
                        verificationUrl +
                                certificate.getCertificateNumber()
                );

                PDImageXObject qrImage =
                        PDImageXObject.createFromByteArray(
                                document,
                                qrBytes,
                                "certificate-qr"
                        );

                // Draw QR code in bottom-right corner.
                content.drawImage(qrImage, 715, 65, 85, 85);

                // QR label.
                content.beginText();
                content.setFont(regular, 9);
                content.newLineAtOffset(710, 52);
                content.showText("Scan to verify");
                content.endText();
            }

            document.save(output);
            return output.toByteArray();

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unable to generate certificate PDF",
                    exception
            );
        }
    }

    // Create QR image containing the verification URL.
    private byte[] generateQrCode(String text)
            throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(
                text,
                BarcodeFormat.QR_CODE,
                250,
                250,
                hints
        );

        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            MatrixToImageWriter.writeToStream(
                    bitMatrix,
                    "PNG",
                    output
            );

            return output.toByteArray();
        }
    }

    private void drawCenteredText(
            PDPageContentStream content,
            PDType1Font font,
            int fontSize,
            String text,
            float pageWidth,
            float y) throws IOException {

        String safeText = makePdfSafe(text);

        float textWidth =
                font.getStringWidth(safeText) / 1000 * fontSize;

        float x = (pageWidth - textWidth) / 2;

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(safeText);
        content.endText();
    }

    private String makePdfSafe(String value) {

        return value.replaceAll("[^\\x20-\\xFF]", "?");
    }
}
