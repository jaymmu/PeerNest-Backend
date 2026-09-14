package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.LectureResponse;
import com.PeerNest.PeerNest.Entity.Lecture;
import com.PeerNest.PeerNest.Service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @PostMapping
    public ResponseEntity<LectureResponse> createLecture(
            @RequestBody com.PeerNest.PeerNest.Dto.LectureRequest request,
            Authentication authentication) {

        Lecture lecture = lectureService.createLecture(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(
                new LectureResponse(
                        lecture.getId(),
                        lecture.getTitle(),
                        lecture.getDescription(),
                        lecture.getVideoUrl(),
                        lecture.getDurationInMinutes(),
                        lecture.getLectureOrder(),
                        lecture.isFreePreview(),
                        lecture.getSection().getId()
                )
        );
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<LectureResponse>> getLectures(
            @PathVariable Long sectionId) {

        List<LectureResponse> response =
                lectureService.getLectures(sectionId)
                        .stream()
                        .map(lecture -> new LectureResponse(
                                lecture.getId(),
                                lecture.getTitle(),
                                lecture.getDescription(),
                                lecture.getVideoUrl(),
                                lecture.getDurationInMinutes(),
                                lecture.getLectureOrder(),
                                lecture.isFreePreview(),
                                lecture.getSection().getId()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLecture(
            @PathVariable Long id,
            Authentication authentication) {

        lectureService.deleteLecture(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Lecture deleted successfully"
        );
    }
}