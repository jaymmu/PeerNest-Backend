package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.CourseRequest;
import com.PeerNest.PeerNest.Dto.CourseResponse;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.CourseStatus;
import com.PeerNest.PeerNest.Entity.Subject;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.SubjectRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Override
    public CourseResponse createCourse(
            CourseRequest request,
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Instructor not found"
                        ));

        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Subject not found"
                        ));

        validatePrice(request);

        Course course = Course.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .price(request.isFree() ? 0 : request.getPrice())
                .free(request.isFree())
                .level(request.getLevel())
                .status(CourseStatus.DRAFT)
                .subject(subject)
                .instructor(instructor)
                .build();

        Course savedCourse =
                courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Override
    public List<CourseResponse> getInstructorCourses(
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Instructor not found"
                        ));

        return courseRepository
                .findByInstructorId(instructor.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CourseResponse getCourseById(
            Long id,
            String instructorEmail) {

        Course course = findCourse(id);

        verifyOwnership(
                course,
                instructorEmail
        );

        return mapToResponse(course);
    }

    @Override
    public CourseResponse updateCourse(
            Long id,
            CourseRequest request,
            String instructorEmail) {

        Course course = findCourse(id);

        verifyOwnership(
                course,
                instructorEmail
        );

        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Subject not found"
                        ));

        validatePrice(request);

        course.setTitle(
                request.getTitle().trim()
        );

        course.setDescription(
                request.getDescription().trim()
        );

        course.setPrice(
                request.isFree()
                        ? 0
                        : request.getPrice()
        );

        course.setFree(
                request.isFree()
        );

        course.setLevel(
                request.getLevel()
        );

        course.setSubject(
                subject
        );

        Course updatedCourse =
                courseRepository.save(course);

        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(
            Long id,
            String instructorEmail) {

        Course course = findCourse(id);

        verifyOwnership(
                course,
                instructorEmail
        );

        courseRepository.delete(course);
    }

    private Course findCourse(Long id) {

        return courseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Course not found"
                        ));
    }

    private void verifyOwnership(
            Course course,
            String instructorEmail) {

        if (course.getInstructor() == null ||
                course.getInstructor().getEmail() == null ||
                !course.getInstructor()
                        .getEmail()
                        .equalsIgnoreCase(instructorEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own courses"
            );
        }
    }

    private void validatePrice(
            CourseRequest request) {

        if (!request.isFree() &&
                request.getPrice() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Paid course price must be greater than 0"
            );
        }
    }

    private CourseResponse mapToResponse(
            Course course) {

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.isFree(),
                course.getLevel(),
                course.getStatus(),
                course.getSubject().getName(),
                course.getInstructor().getName()
        );
    }
}