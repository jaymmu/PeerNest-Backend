
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.CourseReviewResponse;
import com.PeerNest.PeerNest.Dto.CourseRatingSummaryResponse;
import com.PeerNest.PeerNest.Service.CourseReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService reviewService;

    // Student submits a review
    @PostMapping("/api/student/courses/{courseId}/reviews")
    public ResponseEntity<CourseReviewResponse> addReview(
            @PathVariable Long courseId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            Authentication authentication) {

        CourseReviewResponse response = reviewService.addReview(
                courseId,
                rating,
                comment,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    // Get all reviews of a course
    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<List<CourseReviewResponse>> getCourseReviews(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                reviewService.getCourseReviews(courseId)
        );
    }

    // Get course rating summary
    @GetMapping("/api/courses/{courseId}/rating")
    public ResponseEntity<CourseRatingSummaryResponse> getCourseRating(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                reviewService.getCourseRatingSummary(courseId)
        );
    }
}
