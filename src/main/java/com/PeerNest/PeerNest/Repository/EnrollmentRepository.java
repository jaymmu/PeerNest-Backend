
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @EntityGraph(attributePaths = {"course"})
    List<Enrollment> findAllByStudentIdOrderByEnrolledAtDesc(Long studentId);

    long countByStudentId(Long studentId);

    // Compatibility methods used by existing services
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByStudent_Id(Long studentId);
}
