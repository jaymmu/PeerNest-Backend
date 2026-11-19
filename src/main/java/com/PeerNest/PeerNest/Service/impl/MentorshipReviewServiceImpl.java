
package com.PeerNest.PeerNest.Service.impl;
import com.PeerNest.PeerNest.Dto.MentorshipReviewResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.MentorshipReviewRepository;
import com.PeerNest.PeerNest.Repository.MentorshipSessionRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.MentorshipReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipReviewServiceImpl implements MentorshipReviewService {

    private final MentorshipReviewRepository reviewRepository;
    private final MentorshipSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    public MentorshipReview addReview(
            Long sessionId,
            Integer rating,
            String comment,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        MentorshipSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Mentorship session not found"));

        if (!session.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException(
                    "You cannot review another student's session");
        }

        if (session.getStatus() != MentorshipStatus.COMPLETED) {
            throw new RuntimeException(
                    "You can review only completed mentorship sessions");
        }

        if (reviewRepository.existsBySessionId(sessionId)) {
            throw new RuntimeException(
                    "You have already reviewed this session");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException(
                    "Rating must be between 1 and 5");
        }

        if (comment != null && comment.length() > 1000) {
            throw new RuntimeException(
                    "Comment cannot exceed 1000 characters");
        }

        MentorshipReview review = MentorshipReview.builder()
                .rating(rating)
                .comment(comment)
                .session(session)
                .student(student)
                .instructor(session.getInstructor())
                .build();

        return reviewRepository.save(review);
    }

    @Override
    public List<MentorshipReview> getInstructorReviews(Long instructorId) {

        return reviewRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<MentorshipReview> getStudentReviews(String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        return reviewRepository.findByStudentId(student.getId());
    }

    @Override
    public List<MentorshipReviewResponse> getInstructorReviewsByEmail(
            String instructorEmail) {

        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Only instructors can view these reviews");
        }

        return reviewRepository.findByInstructorId(instructor.getId())
                .stream()
                .map(review -> MentorshipReviewResponse.builder()
                        .id(review.getId())
                        .sessionId(review.getSession().getId())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .studentId(review.getStudent().getId())
                        .studentName(review.getStudent().getName())
                        .instructorId(review.getInstructor().getId())
                        .instructorName(review.getInstructor().getName())
                        .createdAt(review.getCreatedAt())
                        .build())
                .toList();
    }

}
