package com.PeerNest.PeerNest.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionRequest {

    @NotBlank(message = "Section title is required")
    private String title;

    private String description;

    @NotNull(message = "Section order is required")
    private Integer sectionOrder;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
