
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Entity.InstructorAvailability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface InstructorAvailabilityService {

    InstructorAvailability addAvailability(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String instructorEmail
    );

    List<InstructorAvailability> getMyAvailability(
            String instructorEmail
    );

    InstructorAvailability updateAvailability(
            Long availabilityId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Boolean available,
            String instructorEmail
    );

    void deleteAvailability(
            Long availabilityId,
            String instructorEmail
    );
    List<InstructorAvailability> getInstructorAvailability(Long instructorId);
}
