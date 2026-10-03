package com.PeerNest.PeerNest.Dto;

import com.PeerNest.PeerNest.Entity.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceRequest {

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @NotBlank(message = "Resource title is required")
    private String title;

    private String description;

    @NotNull(message = "Resource type is required")
    private ResourceType type;
}