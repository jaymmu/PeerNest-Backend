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

        return ResponseEntity.ok(
                courseService.createCourse(
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getMyCourses(
            Authentication authentication) {

        return ResponseEntity.ok(
                courseService.getInstructorCourses(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                courseService.getCourseById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                courseService.updateCourse(
                        id,
                        request,
                        authentication.getName()
                )
        );
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
