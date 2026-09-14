package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgressResponse {

    private Long courseId;

    private String courseTitle;

    private int totalLectures;

    private int completedLectures;

    private double progressPercentage;
}