package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EnrollmentResponse {

    private Long enrollmentId;

    private Long courseId;

    private String courseTitle;

    private String instructor;

    private LocalDateTime enrolledAt;
}