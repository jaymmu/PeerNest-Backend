package com.PeerNest.PeerNest.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LectureRequest {

    @NotBlank(message = "Lecture title is required")
    private String title;

    private String description;

    private String videoUrl;

    private Integer durationInMinutes;

    @NotNull(message = "Lecture order is required")
    private Integer lectureOrder;

    private boolean freePreview;

    @NotNull(message = "Section ID is required")
    private Long sectionId;
}