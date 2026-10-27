package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.CourseResponse;
import com.PeerNest.PeerNest.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping("/pending")
    public ResponseEntity<List<CourseResponse>> getPendingCourses() {
        return ResponseEntity.ok(
                courseService.getPendingCourses()
        );
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveCourse(
            @PathVariable Long id) {

        courseService.approveCourse(id);

        return ResponseEntity.ok(
                "Course approved and published successfully"
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectCourse(
            @PathVariable Long id) {

        courseService.rejectCourse(id);

        return ResponseEntity.ok(
                "Course rejected successfully"
        );
    }
}