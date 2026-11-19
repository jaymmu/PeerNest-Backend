
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.MentorshipReviewResponse;
import com.PeerNest.PeerNest.Entity.MentorshipReview;

import java.util.List;
import com.PeerNest.PeerNest.Dto.MentorshipReviewResponse;
public interface MentorshipReviewService {

    MentorshipReview addReview(
            Long sessionId,
            Integer rating,
            String comment,
            String studentEmail
    );

    List<MentorshipReview> getInstructorReviews(Long instructorId);

    List<MentorshipReview> getStudentReviews(String studentEmail);

    List<MentorshipReviewResponse> getInstructorReviewsByEmail(
            String instructorEmail
    );
}
