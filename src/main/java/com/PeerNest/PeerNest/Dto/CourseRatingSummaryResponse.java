
package com.PeerNest.PeerNest.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRatingSummaryResponse {

    private Long courseId;
    private String courseTitle;
    private Double averageRating;
    private Long totalReviews;
}
