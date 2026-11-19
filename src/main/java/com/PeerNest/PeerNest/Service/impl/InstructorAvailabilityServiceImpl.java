
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.InstructorAvailabilityRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.InstructorAvailabilityService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorAvailabilityServiceImpl
        implements InstructorAvailabilityService {

    private final InstructorAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    // Add instructor availability
    @Override
    public InstructorAvailability addAvailability(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);
        validateTimeSlot(dayOfWeek, startTime, endTime);

        boolean overlaps = availabilityRepository
                .findByInstructorIdAndAvailableTrue(instructor.getId())
                .stream()
                .anyMatch(slot ->
                        slot.getDayOfWeek() == dayOfWeek &&
                                startTime.isBefore(slot.getEndTime()) &&
                                endTime.isAfter(slot.getStartTime())
                );

        if (overlaps) {
            throw new RuntimeException(
                    "This time slot overlaps with existing availability");
        }

        InstructorAvailability availability =
                InstructorAvailability.builder()
                        .instructor(instructor)
                        .dayOfWeek(dayOfWeek)
                        .startTime(startTime)
                        .endTime(endTime)
                        .available(true)
                        .build();

        return availabilityRepository.save(availability);
    }

    // Get instructor's own availability
    @Override
    @Transactional(readOnly = true)
    public List<InstructorAvailability> getMyAvailability(
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        return availabilityRepository
                .findByInstructorId(instructor.getId());
    }

    // Get availability of a particular instructor (for students)
    @Override
    @Transactional(readOnly = true)
    public List<InstructorAvailability> getInstructorAvailability(
            Long instructorId) {

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException(
                    "Selected user is not an instructor");
        }

        return availabilityRepository
                .findByInstructorIdAndAvailableTrue(instructorId);
    }

    // Update availability
    @Override
    public InstructorAvailability updateAvailability(
            Long availabilityId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Boolean available,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);
        validateTimeSlot(dayOfWeek, startTime, endTime);

        InstructorAvailability slot = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() ->
                        new RuntimeException("Availability slot not found"));

        if (!slot.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException(
                    "You cannot update another instructor's availability");
        }

        boolean willBeAvailable = available == null
                ? slot.getAvailable()
                : available;

        if (willBeAvailable) {
            boolean overlaps = availabilityRepository
                    .findByInstructorIdAndAvailableTrue(instructor.getId())
                    .stream()
                    .filter(existing ->
                            !existing.getId().equals(availabilityId))
                    .anyMatch(existing ->
                            existing.getDayOfWeek() == dayOfWeek &&
                                    startTime.isBefore(existing.getEndTime()) &&
                                    endTime.isAfter(existing.getStartTime())
                    );

            if (overlaps) {
                throw new RuntimeException(
                        "This time slot overlaps with existing availability");
            }
        }

        slot.setDayOfWeek(dayOfWeek);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setAvailable(willBeAvailable);

        return availabilityRepository.save(slot);
    }

    // Delete availability
    @Override
    public void deleteAvailability(
            Long availabilityId,
            String instructorEmail) {

        User instructor = getInstructor(instructorEmail);

        InstructorAvailability slot = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() ->
                        new RuntimeException("Availability slot not found"));

        if (!slot.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException(
                    "You cannot delete another instructor's availability");
        }

        availabilityRepository.delete(slot);
    }

    // Check instructor
    private User getInstructor(String email) {

        User instructor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException(
                    "Only instructors can manage availability");
        }

        return instructor;
    }

    // Validate time slot
    private void validateTimeSlot(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime) {

        if (dayOfWeek == null || startTime == null || endTime == null) {
            throw new RuntimeException(
                    "Day, start time and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new RuntimeException(
                    "End time must be after start time");
        }
    }
}
