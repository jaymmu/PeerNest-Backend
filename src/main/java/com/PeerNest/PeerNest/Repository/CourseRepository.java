
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.CourseLevel;
import com.PeerNest.PeerNest.Entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Existing instructor/admin queries
    List<Course> findByStatus(CourseStatus status);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findBySubjectId(Long subjectId);

    List<Course> findByFree(boolean free);

    List<Course> findByTitleContainingIgnoreCase(String keyword);

    // Student course discovery
    List<Course> findByStatusAndTitleContainingIgnoreCase(
            CourseStatus status, String keyword);

    List<Course> findByStatusAndSubjectId(
            CourseStatus status, Long subjectId);

    List<Course> findByStatusAndLevel(
            CourseStatus status, CourseLevel level);

    List<Course> findByStatusAndFree(
            CourseStatus status, boolean free);

    Optional<Course> findByIdAndStatus(
            Long id, CourseStatus status);
}
