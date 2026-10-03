package com.PeerNest.PeerNest.Dto;

import com.PeerNest.PeerNest.Entity.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResourceResponse {

    private Long id;

    private String title;

    private String description;

    private String fileUrl;

    private ResourceType type;

    private Long sectionId;

    private String sectionTitle;

    private LocalDateTime uploadedAt;
}