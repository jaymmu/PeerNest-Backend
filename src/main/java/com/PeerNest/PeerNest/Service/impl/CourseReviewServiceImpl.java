
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.CourseReviewResponse;
import com.PeerNest.PeerNest.Dto.CourseRatingSummaryResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.CourseReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    // Submit a course review
    @Override
    @Transactional
    public CourseReviewResponse addReview(
            Long courseId,
            Integer rating,
            String comment,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException(
                    "Only students can review courses");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException(
                    "Rating must be between 1 and 5");
        }

        if (comment != null && comment.length() > 1000) {
            throw new RuntimeException(
                    "Review cannot exceed 1000 characters");
        }

        boolean enrolled = enrollmentRepository
                .existsByStudentIdAndCourseId(
                        student.getId(),
                        courseId
                );

        if (!enrolled) {
            throw new RuntimeException(
                    "You must enroll in this course before reviewing it");
        }

        if (reviewRepository.existsByCourseIdAndStudentId(
                courseId, student.getId())) {

            throw new RuntimeException(
                    "You have already reviewed this course");
        }

        CourseReview review = CourseReview.builder()
                .course(course)
                .student(student)
                .rating(rating)
                .comment(comment)
                .build();

        CourseReview savedReview =
                reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    // Get all reviews for a course
    @Override
    @Transactional(readOnly = true)
    public List<CourseReviewResponse> getCourseReviews(Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found");
        }

        return reviewRepository
                .findByCourseIdOrderByCreatedAtDesc(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get average rating and review count
    @Override
    @Transactional(readOnly = true)
    public CourseRatingSummaryResponse getCourseRatingSummary(
            Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        List<CourseReview> reviews =
                reviewRepository.findByCourseIdOrderByCreatedAtDesc(
                        courseId);

        double averageRating = reviews.stream()
                .mapToInt(CourseReview::getRating)
                .average()
                .orElse(0.0);

        return CourseRatingSummaryResponse.builder()
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .averageRating(
                        Math.round(averageRating * 100.0) / 100.0)
                .totalReviews((long) reviews.size())
                .build();
    }

    // Convert entity to DTO
    private CourseReviewResponse mapToResponse(
            CourseReview review) {

        return CourseReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .courseTitle(review.getCourse().getTitle())
                .studentId(review.getStudent().getId())
                .studentName(review.getStudent().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
