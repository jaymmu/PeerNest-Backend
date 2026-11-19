
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureProgressRepository
        extends JpaRepository<LectureProgress, Long> {

    long countByStudentIdAndCompletedTrue(Long studentId);

    @Query("""
        SELECT COUNT(lp)
        FROM LectureProgress lp
        WHERE lp.student.id = :studentId
        AND lp.completed = true
        AND lp.lecture.section.course.id = :courseId
    """)
    long countCompletedLectures(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId
    );

    @Query("""
        SELECT COUNT(l)
        FROM Lecture l
        WHERE l.section.course.id = :courseId
    """)
    long countTotalLecturesByCourse(
            @Param("courseId") Long courseId
    );

    @Query("""
        SELECT COUNT(l)
        FROM Lecture l
        JOIN l.section s
        JOIN Enrollment e ON e.course.id = s.course.id
        WHERE e.student.id = :studentId
    """)
    long countLecturesInEnrolledCourses(
            @Param("studentId") Long studentId
    );

    // Methods used by existing services
    Optional<LectureProgress> findByStudentIdAndLectureId(
            Long studentId, Long lectureId
    );

    Optional<LectureProgress> findByStudent_IdAndLecture_Id(
            Long studentId, Long lectureId
    );

    long countByStudentIdAndLectureSectionCourseIdAndCompleted(
            Long studentId, Long courseId, boolean completed
    );

    long countByStudent_IdAndLecture_Section_Course_IdAndCompleted(
            Long studentId, Long courseId, boolean completed
    );
}
