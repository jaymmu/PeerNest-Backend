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
import org.springframework.stereotype.Service;

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
                        new RuntimeException("Instructor not found"));

        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new RuntimeException("Subject not found"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.isFree() ? 0 : request.getPrice())
                .free(request.isFree())
                .level(request.getLevel())
                .status(CourseStatus.DRAFT)
                .subject(subject)
                .instructor(instructor)
                .build();

        Course savedCourse = courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Override
    public List<CourseResponse> getInstructorCourses(
            String instructorEmail) {

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        return courseRepository
                .findByInstructorId(instructor.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CourseResponse getCourseById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        return mapToResponse(course);
    }

    @Override
    public CourseResponse updateCourse(
            Long id,
            CourseRequest request,
            String instructorEmail) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only modify your own courses");
        }

        Subject subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() ->
                        new RuntimeException("Subject not found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(
                request.isFree() ? 0 : request.getPrice()
        );
        course.setFree(request.isFree());
        course.setLevel(request.getLevel());
        course.setSubject(subject);

        Course updatedCourse = courseRepository.save(course);

        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(
            Long id,
            String instructorEmail) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only delete your own courses");
        }

        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course course) {

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
