package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.CourseRequest;
import com.PeerNest.PeerNest.Dto.CourseResponse;
import com.PeerNest.PeerNest.Service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request,
            Authentication authentication) {

        CourseResponse response =
                courseService.createCourse(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            Authentication authentication) {

        List<CourseResponse> courses =
                courseService.getInstructorCourses(
                        authentication.getName()
                );

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(
            @PathVariable Long id,
            Authentication authentication) {

        CourseResponse course =
                courseService.getCourseById(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(course);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request,
            Authentication authentication) {

        CourseResponse response =
                courseService.updateCourse(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id,
            Authentication authentication) {

        courseService.deleteCourse(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Course deleted successfully"
        );
    }
}