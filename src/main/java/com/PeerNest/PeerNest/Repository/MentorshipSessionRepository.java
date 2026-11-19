
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.MentorshipSession;
import com.PeerNest.PeerNest.Entity.MentorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MentorshipSessionRepository
        extends JpaRepository<MentorshipSession, Long> {

    @Query("""
        SELECT m
        FROM MentorshipSession m
        JOIN FETCH m.instructor
        WHERE m.student.id = :studentId
        AND m.scheduledAt >= :now
        AND m.status IN (
            com.PeerNest.PeerNest.Entity.MentorshipStatus.PENDING,
            com.PeerNest.PeerNest.Entity.MentorshipStatus.ACCEPTED
        )
        ORDER BY m.scheduledAt ASC
    """)
    List<MentorshipSession> findUpcomingSessions(
            @Param("studentId") Long studentId,
            @Param("now") LocalDateTime now
    );

    long countByStatus(MentorshipStatus status);

    // Methods used by existing services
    List<MentorshipSession> findByInstructorId(Long instructorId);

    List<MentorshipSession> findByStudentId(Long studentId);

    List<MentorshipSession> findByInstructor_Id(Long instructorId);

    List<MentorshipSession> findByStudent_Id(Long studentId);
}
