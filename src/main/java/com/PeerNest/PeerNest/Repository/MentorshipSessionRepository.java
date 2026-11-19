
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.MentorshipSession;
import com.PeerNest.PeerNest.Entity.MentorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MentorshipSessionRepository
        extends JpaRepository<MentorshipSession, Long> {

    List<MentorshipSession> findByStudentId(Long studentId);

    List<MentorshipSession> findByInstructorId(Long instructorId);

    List<MentorshipSession> findByStudentIdAndStatus(
            Long studentId,
            MentorshipStatus status
    );

    List<MentorshipSession> findByInstructorIdAndStatus(
            Long instructorId,
            MentorshipStatus status
    );

    List<MentorshipSession> findByInstructorIdAndScheduledAtBetweenAndStatus(
            Long instructorId,
            LocalDateTime start,
            LocalDateTime end,
            MentorshipStatus status
    );

    long countByStatus(MentorshipStatus status);
}
