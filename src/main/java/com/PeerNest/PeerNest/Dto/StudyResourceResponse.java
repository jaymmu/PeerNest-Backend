package com.PeerNest.PeerNest.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyResourceResponse {

    private Long id;
    private String title;
    private String description;
    private String type;
    private String fileUrl;
    private String fileName;
    private Integer semester;
    private Integer year;
    private LocalDateTime uploadedAt;
    private Long courseId;
    private String uploadedBy;
}