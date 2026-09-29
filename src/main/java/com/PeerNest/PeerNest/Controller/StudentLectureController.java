package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.LectureResponse;
import com.PeerNest.PeerNest.Service.LectureService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/lectures")
@RequiredArgsConstructor
public class StudentLectureController {

    private final LectureService lectureService;

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<LectureResponse>> getLectures(
            @PathVariable Long sectionId,
            Authentication authentication) {

        String studentEmail = authentication.getName();

        List<LectureResponse> response =
                lectureService.getStudentLectures(
                                sectionId,
                                studentEmail
                        )
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
}