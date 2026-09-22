package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.SectionResponse;
import com.PeerNest.PeerNest.Service.SectionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/sections")
@RequiredArgsConstructor
public class StudentSectionController {

    private final SectionService sectionService;


    // =========================================================
    // GET SECTIONS OF COURSE
    // GET /api/student/sections/course/{courseId}
    // =========================================================

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<SectionResponse>> getSections(
            @PathVariable Long courseId) {

        List<SectionResponse> response =
                sectionService
                        .getSections(courseId)
                        .stream()
                        .map(section ->
                                new SectionResponse(
                                        section.getId(),
                                        section.getTitle(),
                                        section.getDescription(),
                                        section.getSectionOrder(),
                                        section.getCourse().getId()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }
}