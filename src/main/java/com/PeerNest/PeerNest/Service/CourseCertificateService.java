
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.CertificateVerificationResponse;

public interface CourseCertificateService {

    byte[] downloadCertificate(
            Long courseId,
            String studentEmail
    );

    CertificateVerificationResponse verifyCertificate(
            String certificateNumber
    );
}
