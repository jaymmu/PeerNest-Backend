
package com.PeerNest.PeerNest.Dto;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorAvailabilityResponse {

    private Long id;
    private Long instructorId;
    private String instructorName;

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private Boolean available;
}
