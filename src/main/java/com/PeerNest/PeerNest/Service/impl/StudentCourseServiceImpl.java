
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.StudentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCourseServiceImpl implements StudentCourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    // Get all published courses
    @Override
    public List<StudentCourseResponse> getPublishedCourses() {
        return courseRepository.findByStatus(CourseStatus.PUBLISHED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get one published course
    @Override
    public StudentCourseResponse getCourseById(Long courseId) {
        Course course = courseRepository
                .findByIdAndStatus(courseId, CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published course not found"));

        return mapToResponse(course);
    }

    // Search published courses by title
    @Override
    public List<StudentCourseResponse> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search keyword cannot be empty");
        }

        return courseRepository
                .findByStatusAndTitleContainingIgnoreCase(
                        CourseStatus.PUBLISHED, keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Filter by subject ID
    @Override
    public List<StudentCourseResponse> getCoursesBySubject(Long subjectId) {
        return courseRepository
                .findByStatusAndSubjectId(
                        CourseStatus.PUBLISHED, subjectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Filter by course level
    @Override
    public List<StudentCourseResponse> getCoursesByLevel(CourseLevel level) {
        return courseRepository
                .findByStatusAndLevel(CourseStatus.PUBLISHED, level)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Filter by free or paid status
    @Override
    public List<StudentCourseResponse> getCoursesByFreeStatus(boolean free) {
        return courseRepository
                .findByStatusAndFree(CourseStatus.PUBLISHED, free)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Enroll a student in a published course
    @Override
    @Transactional
    public EnrollmentResponse enroll(Long courseId, String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        Course course = courseRepository
                .findByIdAndStatus(courseId, CourseStatus.PUBLISHED)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Published course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), courseId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        return new EnrollmentResponse(
                saved.getId(),
                course.getId(),
                course.getTitle(),
                course.getInstructor().getName(),
                saved.getEnrolledAt()
        );
    }

    // Get the current student's enrollments
    @Override
    public List<EnrollmentResponse> getMyEnrollments(String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        return enrollmentRepository.findByStudentId(student.getId())
                .stream()
                .map(enrollment -> new EnrollmentResponse(
                        enrollment.getId(),
                        enrollment.getCourse().getId(),
                        enrollment.getCourse().getTitle(),
                        enrollment.getCourse().getInstructor().getName(),
                        enrollment.getEnrolledAt()
                ))
                .toList();
    }

    // Map entity to student-facing DTO
    private StudentCourseResponse mapToResponse(Course course) {
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
