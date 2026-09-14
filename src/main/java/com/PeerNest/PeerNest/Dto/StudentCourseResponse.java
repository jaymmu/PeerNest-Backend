package com.PeerNest.PeerNest.Dto;

import com.PeerNest.PeerNest.Entity.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCourseResponse {

    private Long id;

    private String title;

    private String description;

    private double price;

    private boolean free;

    private CourseLevel level;

    private String subject;

    private String instructor;

    private String thumbnailUrl;
}