package com.PeerNest.PeerNest.Repository;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStatus(CourseStatus status);

    List<Course> findByInstructorId(Long instructorId);

    List<Course> findBySubjectId(Long subjectId);

    List<Course> findByFree(boolean free);

    List<Course> findByTitleContainingIgnoreCase(String keyword);
}