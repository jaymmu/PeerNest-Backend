
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.CourseReviewResponse;
import com.PeerNest.PeerNest.Dto.CourseRatingSummaryResponse;

import java.util.List;

public interface CourseReviewService {

    CourseReviewResponse addReview(
            Long courseId,
            Integer rating,
            String comment,
            String studentEmail
    );

    List<CourseReviewResponse> getCourseReviews(Long courseId);

    CourseRatingSummaryResponse getCourseRatingSummary(Long courseId);
}
