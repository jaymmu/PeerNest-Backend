
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.CourseCertificateService;

import lombok.RequiredArgsConstructor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        // Calculate total lectures in the course.
        int totalLectures = course.getSections()
                .stream()
                .mapToInt(section ->
                        section.getLectures().size())
                .sum();

        // Calculate completed lectures for this student.
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

        // Reuse an existing certificate if already generated.
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

                // Outer border
                content.setLineWidth(3);
                content.addRect(25, 25, 792, 545);
                content.stroke();

                // Inner border
                content.setLineWidth(1);
                content.addRect(35, 35, 772, 525);
                content.stroke();

                drawCenteredText(
                        content,
                        bold,
                        30,
                        "PeerNest",
                        595,
                        500
                );

                drawCenteredText(
                        content,
                        bold,
                        32,
                        "CERTIFICATE OF COMPLETION",
                        595,
                        420
                );

                drawCenteredText(
                        content,
                        regular,
                        17,
                        "This certificate is proudly presented to",
                        595,
                        355
                );

                drawCenteredText(
                        content,
                        bold,
                        28,
                        certificate.getStudent().getName(),
                        595,
                        305
                );

                drawCenteredText(
                        content,
                        regular,
                        17,
                        "for successfully completing the course",
                        595,
                        260
                );

                drawCenteredText(
                        content,
                        bold,
                        24,
                        certificate.getCourse().getTitle(),
                        595,
                        215
                );

                String date = certificate.getIssueDate()
                        .format(DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy"
                        ));

                drawCenteredText(
                        content,
                        regular,
                        13,
                        "Completion Date: " + date,
                        595,
                        145
                );

                drawCenteredText(
                        content,
                        regular,
                        11,
                        "Certificate ID: " +
                                certificate.getCertificateNumber(),
                        595,
                        105
                );

                drawCenteredText(
                        content,
                        bold,
                        13,
                        "PeerNest Learning Platform",
                        595,
                        65
                );
            }

            document.save(output);
            return output.toByteArray();

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Unable to generate certificate PDF",
                    exception
            );
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

        // Standard PDF fonts support limited character sets.
        return value.replaceAll("[^\\x20-\\xFF]", "?");
    }
}
