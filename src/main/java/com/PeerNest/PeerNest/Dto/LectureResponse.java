package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LectureResponse {

    private Long id;

    private String title;

    private String description;

    private String videoUrl;

    private Integer durationInMinutes;

    private Integer lectureOrder;

    private Boolean freePreview;

    private Long sectionId;
}