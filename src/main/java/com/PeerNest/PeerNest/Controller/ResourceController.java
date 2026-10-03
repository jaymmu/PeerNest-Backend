package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.ResourceRequest;
import com.PeerNest.PeerNest.Dto.ResourceResponse;
import com.PeerNest.PeerNest.Service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // =========================================================
    // INSTRUCTOR - UPLOAD RESOURCE
    // =========================================================

    @PostMapping(
            value = "/api/instructor/resources",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResourceResponse> uploadResource(
            @Valid @ModelAttribute ResourceRequest request,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        ResourceResponse response =
                resourceService.uploadResource(
                        request,
                        file,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // INSTRUCTOR - GET RESOURCES
    // =========================================================

    @GetMapping(
            "/api/instructor/resources/section/{sectionId}"
    )
    public ResponseEntity<List<ResourceResponse>> getInstructorResources(
            @PathVariable Long sectionId,
            Authentication authentication
    ) {

        List<ResourceResponse> resources =
                resourceService.getInstructorResources(
                        sectionId,
                        authentication.getName()
                );

        return ResponseEntity.ok(resources);
    }

    // =========================================================
    // INSTRUCTOR - DELETE RESOURCE
    // =========================================================

    @DeleteMapping(
            "/api/instructor/resources/{resourceId}"
    )
    public ResponseEntity<String> deleteResource(
            @PathVariable Long resourceId,
            Authentication authentication
    ) {

        resourceService.deleteResource(
                resourceId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Resource deleted successfully"
        );
    }

    // =========================================================
    // STUDENT - GET RESOURCES
    // =========================================================

    @GetMapping(
            "/api/student/resources/section/{sectionId}"
    )
    public ResponseEntity<List<ResourceResponse>> getStudentResources(
            @PathVariable Long sectionId,
            Authentication authentication
    ) {

        List<ResourceResponse> resources =
                resourceService.getStudentResources(
                        sectionId,
                        authentication.getName()
                );

        return ResponseEntity.ok(resources);
    }
}