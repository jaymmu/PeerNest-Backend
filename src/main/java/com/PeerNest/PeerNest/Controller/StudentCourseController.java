
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;
import com.PeerNest.PeerNest.Entity.CourseLevel;
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

    // Browse all published courses
    @GetMapping("/courses")
    public ResponseEntity<List<StudentCourseResponse>> getCourses() {
        return ResponseEntity.ok(
                studentCourseService.getPublishedCourses());
    }

    // View a published course
    @GetMapping("/courses/{id}")
    public ResponseEntity<StudentCourseResponse> getCourse(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                studentCourseService.getCourseById(id));
    }

    // Search published courses by title
    @GetMapping("/courses/search")
    public ResponseEntity<List<StudentCourseResponse>> searchCourses(
            @RequestParam String keyword) {
        return ResponseEntity.ok(
                studentCourseService.searchCourses(keyword));
    }

    // Filter by subject ID
    @GetMapping("/courses/subject/{subjectId}")
    public ResponseEntity<List<StudentCourseResponse>> getCoursesBySubject(
            @PathVariable Long subjectId) {
        return ResponseEntity.ok(
                studentCourseService.getCoursesBySubject(subjectId));
    }

    // Filter by level, e.g. BEGINNER
    @GetMapping("/courses/level/{level}")
    public ResponseEntity<List<StudentCourseResponse>> getCoursesByLevel(
            @PathVariable CourseLevel level) {
        return ResponseEntity.ok(
                studentCourseService.getCoursesByLevel(level));
    }

    // Filter: true = free, false = paid
    @GetMapping("/courses/free/{free}")
    public ResponseEntity<List<StudentCourseResponse>> getCoursesByFreeStatus(
            @PathVariable boolean free) {
        return ResponseEntity.ok(
                studentCourseService.getCoursesByFreeStatus(free));
    }

    // Enroll the authenticated student
    @PostMapping("/student/enrollments/{courseId}")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable Long courseId,
            Authentication authentication) {
        return ResponseEntity.ok(
                studentCourseService.enroll(
                        courseId, authentication.getName()));
    }

    // Get the authenticated student's enrollments
    @GetMapping("/student/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            Authentication authentication) {
        return ResponseEntity.ok(
                studentCourseService.getMyEnrollments(
                        authentication.getName()));
    }
}
