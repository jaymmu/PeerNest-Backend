package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;
import com.PeerNest.PeerNest.Service.StudentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentCourseController {

    private final StudentCourseService studentCourseService;

    // Anyone can browse published courses
    @GetMapping("/courses")
    public ResponseEntity<List<StudentCourseResponse>>
    getCourses() {

        return ResponseEntity.ok(
                studentCourseService.getPublishedCourses()
        );
    }

    // Anyone can view a published course
    @GetMapping("/courses/{id}")
    public ResponseEntity<StudentCourseResponse>
    getCourse(@PathVariable Long id) {

        return ResponseEntity.ok(
                studentCourseService.getCourseById(id)
        );
    }

    // Search courses
    @GetMapping("/courses/search")
    public ResponseEntity<List<StudentCourseResponse>>
    searchCourses(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                studentCourseService
                        .searchCourses(keyword)
        );
    }

    // Student enrollment
    @PostMapping("/student/enrollments/{courseId}")
    public ResponseEntity<EnrollmentResponse>
    enroll(
            @PathVariable Long courseId,
            Authentication authentication) {

        return ResponseEntity.ok(
                studentCourseService.enroll(
                        courseId,
                        authentication.getName()
                )
        );
    }

    // Student's courses
    @GetMapping("/student/enrollments")
    public ResponseEntity<List<EnrollmentResponse>>
    getMyEnrollments(
            Authentication authentication) {

        return ResponseEntity.ok(
                studentCourseService
                        .getMyEnrollments(
                                authentication.getName()
                        )
        );
    }
}