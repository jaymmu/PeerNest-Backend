
package com.PeerNest.PeerNest.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipReviewResponse {

    private Long id;
    private Long sessionId;
    private Integer rating;
    private String comment;

    private Long studentId;
    private String studentName;

    private Long instructorId;
    private String instructorName;

    private LocalDateTime createdAt;
}
