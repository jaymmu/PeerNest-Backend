
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.CourseCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseCertificateRepository
        extends JpaRepository<CourseCertificate, Long> {

    List<CourseCertificate> findAllByStudentIdOrderByIssueDateDesc(
            Long studentId
    );

    Optional<CourseCertificate> findByStudentIdAndCourseId(
            Long studentId, Long courseId
    );

    Optional<CourseCertificate> findByStudent_IdAndCourse_Id(
            Long studentId, Long courseId
    );

    Optional<CourseCertificate> findByCertificateNumber(
            String certificateNumber
    );
}
