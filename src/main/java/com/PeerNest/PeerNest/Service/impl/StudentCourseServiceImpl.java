package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.StudentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentCourseServiceImpl
        implements StudentCourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    public List<StudentCourseResponse> getPublishedCourses() {

        return courseRepository
                .findByStatus(CourseStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public StudentCourseResponse getCourseById(
            Long courseId) {

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException(
                    "Course is not available"
            );
        }

        return mapToResponse(course);
    }

    @Override
    public List<StudentCourseResponse> searchCourses(
            String keyword) {

        return courseRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .filter(course ->
                        course.getStatus()
                                == CourseStatus.PUBLISHED)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EnrollmentResponse enroll(
            Long courseId,
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException(
                    "You cannot enroll in this course"
            );
        }

        if (enrollmentRepository
                .existsByStudentIdAndCourseId(
                        student.getId(),
                        courseId)) {

            throw new RuntimeException(
                    "Already enrolled in this course"
            );
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .build();

        Enrollment saved =
                enrollmentRepository.save(enrollment);

        return new EnrollmentResponse(
                saved.getId(),
                course.getId(),
                course.getTitle(),
                course.getInstructor().getName(),
                saved.getEnrolledAt()
        );
    }

    @Override
    public List<EnrollmentResponse> getMyEnrollments(
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        return enrollmentRepository
                .findByStudentId(student.getId())
                .stream()
                .map(enrollment ->
                        new EnrollmentResponse(
                                enrollment.getId(),
                                enrollment.getCourse().getId(),
                                enrollment.getCourse().getTitle(),
                                enrollment.getCourse()
                                        .getInstructor()
                                        .getName(),
                                enrollment.getEnrolledAt()
                        )
                )
                .toList();
    }

    private StudentCourseResponse mapToResponse(
            Course course) {

        return new StudentCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.isFree(),
                course.getLevel(),
                course.getSubject().getName(),
                course.getInstructor().getName(),
                course.getThumbnailUrl()
        );
    }
}