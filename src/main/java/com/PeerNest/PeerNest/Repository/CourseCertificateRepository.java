
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.CourseCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseCertificateRepository
        extends JpaRepository<CourseCertificate, Long> {

    Optional<CourseCertificate> findByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    Optional<CourseCertificate> findByCertificateNumber(
            String certificateNumber
    );
}
