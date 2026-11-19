
package com.PeerNest.PeerNest.Dto;

import java.time.LocalDate;

public record CertificateVerificationResponse(
        boolean valid,
        String certificateNumber,
        String studentName,
        String courseTitle,
        LocalDate issueDate,
        String message
) {
}
