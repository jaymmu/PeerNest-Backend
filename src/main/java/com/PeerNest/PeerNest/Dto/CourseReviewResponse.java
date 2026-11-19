
package com.PeerNest.PeerNest.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewResponse {

    private Long id;
    private Long courseId;
    private String courseTitle;

    private Long studentId;
    private String studentName;

    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
