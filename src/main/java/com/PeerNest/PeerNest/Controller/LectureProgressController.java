package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.ProgressResponse;
import com.PeerNest.PeerNest.Service.LectureProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/progress")
@RequiredArgsConstructor
public class LectureProgressController {

    private final LectureProgressService progressService;

    @PostMapping("/lecture/{lectureId}/complete")
    public ResponseEntity<String> markComplete(
            @PathVariable Long lectureId,
            Authentication authentication) {

        progressService.markLectureComplete(
                lectureId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Lecture marked as completed"
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ProgressResponse> getProgress(
            @PathVariable Long courseId,
            Authentication authentication) {

        return ResponseEntity.ok(
                progressService.getCourseProgress(
                        courseId,
                        authentication.getName()
                )
        );
    }
}