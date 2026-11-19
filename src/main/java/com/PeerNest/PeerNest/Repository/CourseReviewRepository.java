
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseReviewRepository
        extends JpaRepository<CourseReview, Long> {

    List<CourseReview> findByCourseIdOrderByCreatedAtDesc(
            Long courseId
    );

    boolean existsByCourseIdAndStudentId(
            Long courseId,
            Long studentId
    );

    List<CourseReview> findByStudentId(Long studentId);

    long countByCourseId(Long courseId);
}
