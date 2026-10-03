package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.Resource;
import com.PeerNest.PeerNest.Entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository
        extends JpaRepository<Resource, Long> {

    List<Resource> findBySectionIdOrderByUploadedAtDesc(
            Long sectionId
    );

    List<Resource> findBySectionIdAndTypeOrderByUploadedAtDesc(
            Long sectionId,
            ResourceType type
    );
}