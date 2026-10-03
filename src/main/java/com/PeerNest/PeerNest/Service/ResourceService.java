package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.ResourceRequest;
import com.PeerNest.PeerNest.Dto.ResourceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    ResourceResponse uploadResource(
            ResourceRequest request,
            MultipartFile file,
            String instructorEmail
    );

    List<ResourceResponse> getInstructorResources(
            Long sectionId,
            String instructorEmail
    );

    List<ResourceResponse> getStudentResources(
            Long sectionId,
            String studentEmail
    );

    void deleteResource(
            Long resourceId,
            String instructorEmail
    );
}