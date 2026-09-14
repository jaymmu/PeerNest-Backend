package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureProgressRepository
        extends JpaRepository<LectureProgress, Long> {

    Optional<LectureProgress> findByStudentIdAndLectureId(
            Long studentId,
            Long lectureId
    );

    List<LectureProgress> findByStudentIdAndLectureSectionCourseId(
            Long studentId,
            Long courseId
    );

    long countByStudentIdAndLectureSectionCourseIdAndCompleted(
            Long studentId,
            Long courseId,
            boolean completed
    );
}