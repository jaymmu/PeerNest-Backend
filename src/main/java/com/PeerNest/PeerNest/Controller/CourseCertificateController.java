
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Service.CourseCertificateService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseCertificateController {

    private final CourseCertificateService certificateService;

    @GetMapping("/course/{courseId}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long courseId,
            Authentication authentication) {

        byte[] pdf = certificateService.downloadCertificate(
                courseId,
                authentication.getName()
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=PeerNest-Certificate.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}
