package com.PeerNest.PeerNest.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SectionRequest {

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Section title is required")
    private String title;

    @NotBlank(message = "Section description is required")
    private String description;

    @NotNull(message = "Section order is required")
    @Min(value = 1, message = "Section order must be at least 1")
    private Integer sectionOrder;
}