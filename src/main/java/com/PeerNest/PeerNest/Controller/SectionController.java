package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.SectionRequest;
import com.PeerNest.PeerNest.Dto.SectionResponse;
import com.PeerNest.PeerNest.Entity.Section;
import com.PeerNest.PeerNest.Service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @Valid @RequestBody SectionRequest request,
            Authentication authentication) {

        String instructorEmail = authentication.getName();

        Section section = sectionService.createSection(
                request,
                instructorEmail
        );

        SectionResponse response = new SectionResponse(
                section.getId(),
                section.getTitle(),
                section.getDescription(),
                section.getSectionOrder(),
                section.getCourse().getId()
        );

        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(
            @PathVariable Long id,
            Authentication authentication) {

        sectionService.deleteSection(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Section deleted successfully"
        );
    }
}