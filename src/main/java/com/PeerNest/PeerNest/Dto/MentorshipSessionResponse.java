
package com.PeerNest.PeerNest.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorshipSessionResponse {

    private Long id;
    private String topic;
    private String description;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String meetingLink;
    private String status;

    private Long studentId;
    private String studentName;

    private Long instructorId;
    private String instructorName;

    private LocalDateTime createdAt;
}
