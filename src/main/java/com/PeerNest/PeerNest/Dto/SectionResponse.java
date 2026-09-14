package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SectionResponse {

    private Long id;

    private String title;

    private String description;

    private Integer sectionOrder;

    private Long courseId;
}