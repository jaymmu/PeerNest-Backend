
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.StudyResourceResponse;
import com.PeerNest.PeerNest.Entity.StudyResource;
import com.PeerNest.PeerNest.Entity.ResourceType;
import com.PeerNest.PeerNest.Service.StudyResourceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyResourceController {

    private final StudyResourceService studyResourceService;

    // 1. Instructor: Upload Study Notes or PYQs
    @PostMapping(
            value = "/api/instructor/courses/{courseId}/resources",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<StudyResourceResponse> uploadResource(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("type") ResourceType type,
            @RequestParam(value = "semester", required = false) Integer semester,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) throws IOException {

        StudyResource resource = studyResourceService.uploadResource(
                courseId,
                title,
                description,
                type,
                semester,
                year,
                file,
                authentication.getName()
        );

        return ResponseEntity.ok(toResponse(resource));
    }

    // 2. Student: Get resources for an enrolled course
    @GetMapping("/api/student/courses/{courseId}/resources")
    public ResponseEntity<List<StudyResourceResponse>> getStudentResources(
            @PathVariable Long courseId,
            Authentication authentication
    ) {

        List<StudyResourceResponse> resources =
                studyResourceService.getCourseResources(
                                courseId,
                                authentication.getName()
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(resources);
    }

    // 3. Instructor: Get uploaded resources
    @GetMapping("/api/instructor/courses/{courseId}/resources")
    public ResponseEntity<List<StudyResourceResponse>> getInstructorResources(
            @PathVariable Long courseId,
            Authentication authentication
    ) {

        List<StudyResourceResponse> resources =
                studyResourceService.getCourseResources(
                                courseId,
                                authentication.getName()
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(resources);
    }

    // 4. Instructor: Delete a study resource
    // Different URL avoids conflict with existing ResourceController
    @DeleteMapping("/api/instructor/study-resources/{resourceId}")
    public ResponseEntity<String> deleteResource(
            @PathVariable Long resourceId,
            Authentication authentication
    ) throws Exception {

        studyResourceService.deleteResource(
                resourceId,
                authentication.getName()
        );

        return ResponseEntity.ok("Study resource deleted successfully");
    }

    // Convert Entity into DTO
    private StudyResourceResponse toResponse(StudyResource resource) {

        return StudyResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .type(resource.getType().name())
                .fileUrl(resource.getFileUrl())
                .fileName(resource.getFileName())
                .semester(resource.getSemester())
                .year(resource.getYear())
                .uploadedAt(resource.getUploadedAt())
                .courseId(resource.getCourse().getId())
                .uploadedBy(resource.getUploadedBy().getName())
                .build();
    }
}
