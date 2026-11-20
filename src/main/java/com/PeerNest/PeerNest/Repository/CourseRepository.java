
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.CourseLevel;
import com.PeerNest.PeerNest.Entity.CourseStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find courses by status
    List<Course> findByStatus(CourseStatus status);

    // Find courses by instructor
    List<Course> findByInstructorId(Long instructorId);

    // Find instructor's courses by status
    List<Course> findByInstructorIdAndStatus(
            Long instructorId,
            CourseStatus status
    );

    // Find a course belonging to a specific instructor
    Optional<Course> findByIdAndInstructorId(
            Long courseId,
            Long instructorId
    );

    // Find courses by subject
    List<Course> findBySubjectId(Long subjectId);

    // Find free or paid courses
    List<Course> findByFree(boolean free);

    // Search courses by title
    List<Course> findByTitleContainingIgnoreCase(String keyword);

    // Student course discovery
    List<Course> findByStatusAndTitleContainingIgnoreCase(
            CourseStatus status,
            String keyword
    );

    List<Course> findByStatusAndSubjectId(
            CourseStatus status,
            Long subjectId
    );

    List<Course> findByStatusAndLevel(
            CourseStatus status,
            CourseLevel level
    );

    List<Course> findByStatusAndFree(
            CourseStatus status,
            boolean free
    );

    // Find a course by ID and status
    Optional<Course> findByIdAndStatus(
            Long id,
            CourseStatus status
    );
}
