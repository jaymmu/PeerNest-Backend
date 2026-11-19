
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.MentorshipReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorshipReviewRepository
        extends JpaRepository<MentorshipReview, Long> {

    Optional<MentorshipReview> findBySessionId(Long sessionId);

    List<MentorshipReview> findByInstructorId(Long instructorId);

    List<MentorshipReview> findByStudentId(Long studentId);

    boolean existsBySessionId(Long sessionId);
}
