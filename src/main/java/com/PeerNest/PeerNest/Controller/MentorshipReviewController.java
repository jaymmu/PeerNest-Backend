
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.MentorshipReviewResponse;
import com.PeerNest.PeerNest.Entity.MentorshipReview;
import com.PeerNest.PeerNest.Service.MentorshipReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class MentorshipReviewController {

    private final MentorshipReviewService reviewService;

    // Student submits a review
    @PostMapping("/student/reviews/{sessionId}")
    public ResponseEntity<MentorshipReviewResponse> addReview(
            @PathVariable Long sessionId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            Authentication authentication) {

        MentorshipReview review = reviewService.addReview(
                sessionId,
                rating,
                comment,
                authentication.getName()
        );

        return ResponseEntity.ok(mapToResponse(review));
    }

    // Instructor views received reviews
    @GetMapping("/instructor/reviews")
    public ResponseEntity<List<MentorshipReviewResponse>> getInstructorReviews(
            Authentication authentication) {

        List<MentorshipReviewResponse> reviews =
                reviewService.getInstructorReviewsByEmail(
                        authentication.getName()
                );

        return ResponseEntity.ok(reviews);
    }

    // Student views submitted reviews
    @GetMapping("/student/my-reviews")
    public ResponseEntity<List<MentorshipReviewResponse>> getStudentReviews(
            Authentication authentication) {

        List<MentorshipReview> reviews =
                reviewService.getStudentReviews(authentication.getName());

        return ResponseEntity.ok(
                reviews.stream().map(this::mapToResponse).toList()
        );
    }

    private MentorshipReviewResponse mapToResponse(
            MentorshipReview review) {

        return MentorshipReviewResponse.builder()
                .id(review.getId())
                .sessionId(review.getSession().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getName())
                .instructorId(review.getInstructor().getId())
                .instructorName(review.getInstructor().getName())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
