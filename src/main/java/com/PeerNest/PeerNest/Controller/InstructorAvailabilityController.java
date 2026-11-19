
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.InstructorAvailabilityResponse;
import com.PeerNest.PeerNest.Entity.InstructorAvailability;
import com.PeerNest.PeerNest.Service.InstructorAvailabilityService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class InstructorAvailabilityController {

    private final InstructorAvailabilityService availabilityService;

    // Instructor adds availability
    @PostMapping("/instructor/availability")
    public ResponseEntity<InstructorAvailabilityResponse> addAvailability(
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            Authentication authentication) {

        InstructorAvailability slot =
                availabilityService.addAvailability(
                        dayOfWeek,
                        startTime,
                        endTime,
                        authentication.getName()
                );

        return ResponseEntity.ok(toResponse(slot));
    }

    // Instructor views own availability
    @GetMapping("/instructor/availability/my-slots")
    public ResponseEntity<List<InstructorAvailabilityResponse>> getMyAvailability(
            Authentication authentication) {

        List<InstructorAvailabilityResponse> slots =
                availabilityService
                        .getMyAvailability(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(slots);
    }

    // Student views a particular instructor's available slots
    @GetMapping("/student/instructors/{instructorId}/availability")
    public ResponseEntity<List<InstructorAvailabilityResponse>>
    getInstructorAvailability(@PathVariable Long instructorId) {

        List<InstructorAvailabilityResponse> slots =
                availabilityService
                        .getInstructorAvailability(instructorId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(slots);
    }

    // Instructor updates availability
    @PutMapping("/instructor/availability/{availabilityId}")
    public ResponseEntity<InstructorAvailabilityResponse> updateAvailability(
            @PathVariable Long availabilityId,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam(required = false) Boolean available,
            Authentication authentication) {

        InstructorAvailability slot =
                availabilityService.updateAvailability(
                        availabilityId,
                        dayOfWeek,
                        startTime,
                        endTime,
                        available,
                        authentication.getName()
                );

        return ResponseEntity.ok(toResponse(slot));
    }

    // Instructor deletes availability
    @DeleteMapping("/instructor/availability/{availabilityId}")
    public ResponseEntity<String> deleteAvailability(
            @PathVariable Long availabilityId,
            Authentication authentication) {

        availabilityService.deleteAvailability(
                availabilityId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Availability deleted successfully"
        );
    }

    private InstructorAvailabilityResponse toResponse(
            InstructorAvailability slot) {

        return InstructorAvailabilityResponse.builder()
                .id(slot.getId())
                .instructorId(slot.getInstructor().getId())
                .instructorName(slot.getInstructor().getName())
                .dayOfWeek(slot.getDayOfWeek())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .available(slot.getAvailable())
                .build();
    }
}
