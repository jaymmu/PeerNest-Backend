
package com.PeerNest.PeerNest.Service;

public interface CourseCertificateService {

    byte[] downloadCertificate(
            Long courseId,
            String studentEmail
    );
}
