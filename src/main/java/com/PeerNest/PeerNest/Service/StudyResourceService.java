
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Entity.ResourceType;
import com.PeerNest.PeerNest.Entity.StudyResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StudyResourceService {

    StudyResource uploadResource(
            Long courseId,
            String title,
            String description,
            ResourceType type,
            Integer semester,
            Integer year,
            MultipartFile file,
            String userEmail
    ) throws IOException;

    List<StudyResource> getCourseResources(
            Long courseId,
            String userEmail
    );

    void deleteResource(
            Long resourceId,
            String userEmail
    ) throws Exception;
}

