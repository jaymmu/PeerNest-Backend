
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.MentorshipSessionService;
import com.PeerNest.PeerNest.Service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipSessionServiceImpl
        implements MentorshipSessionService {

    private final MentorshipSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final InstructorAvailabilityRepository availabilityRepository;
    private final NotificationService notificationService;

    // 1. Book mentorship session
    @Override
    @Transactional
    public MentorshipSession bookSession(
            Long instructorId,
            String topic,
            String description,
            LocalDateTime scheduledAt,
            Integer durationMinutes,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new RuntimeException("Selected user is not an instructor");
        }

        if (topic == null || topic.isBlank()) {
            throw new RuntimeException("Topic is required");
        }

        if (scheduledAt == null || !scheduledAt.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Select a future date and time");
        }

        if (durationMinutes == null ||
                durationMinutes < 15 ||
                durationMinutes > 120) {
            throw new RuntimeException(
                    "Session duration must be between 15 and 120 minutes");
        }

        LocalDateTime sessionEnd =
                scheduledAt.plusMinutes(durationMinutes);

        // Check instructor availability
        boolean available = availabilityRepository
                .findByInstructorIdAndAvailableTrue(instructorId)
                .stream()
                .anyMatch(slot ->
                        slot.getDayOfWeek() == scheduledAt.getDayOfWeek()
                                && !scheduledAt.toLocalTime()
                                .isBefore(slot.getStartTime())
                                && !sessionEnd.toLocalTime()
                                .isAfter(slot.getEndTime())
                );

        if (!available) {
            throw new RuntimeException(
                    "Instructor is not available at the selected time");
        }

        // Check overlapping pending or accepted sessions
        List<MentorshipSession> existingSessions =
                sessionRepository.findByInstructorId(instructorId);

        boolean conflict = existingSessions.stream()
                .filter(session ->
                        session.getStatus() == MentorshipStatus.PENDING
                                || session.getStatus() == MentorshipStatus.ACCEPTED)
                .anyMatch(session -> {
                    LocalDateTime existingStart = session.getScheduledAt();
                    LocalDateTime existingEnd = existingStart.plusMinutes(
                            session.getDurationMinutes());

                    return scheduledAt.isBefore(existingEnd)
                            && sessionEnd.isAfter(existingStart);
                });

        if (conflict) {
            throw new RuntimeException(
                    "Instructor already has a session at this time");
        }

        MentorshipSession session = MentorshipSession.builder()
                .topic(topic)
                .description(description)
                .scheduledAt(scheduledAt)
                .durationMinutes(durationMinutes)
                .student(student)
                .instructor(instructor)
                .status(MentorshipStatus.PENDING)
                .build();

        MentorshipSession savedSession =
                sessionRepository.save(session);

        // Notify instructor
        notificationService.createNotification(
                instructor.getId(),
                "New Mentorship Booking",
                student.getName() + " booked a mentorship session: " + topic,
                "MENTORSHIP_BOOKING"
        );

        return savedSession;
    }

    // 2. Get student sessions
    @Override
    public List<MentorshipSession> getStudentSessions(String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        return sessionRepository.findByStudentId(student.getId());
    }

    // 3. Get instructor sessions
    @Override
    public List<MentorshipSession> getInstructorSessions(
            String instructorEmail) {

        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        return sessionRepository.findByInstructorId(instructor.getId());
    }

    // 4. Accept or reject a session
    @Override
    @Transactional
    public MentorshipSession updateSessionStatus(
            Long sessionId,
            String status,
            String meetingLink,
            String instructorEmail) {

        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        MentorshipSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Session not found"));

        if (!session.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException(
                    "You cannot update another instructor's session");
        }

        if (session.getStatus() != MentorshipStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending sessions can be updated");
        }

        if (status == null ||
                (!status.equalsIgnoreCase("ACCEPTED")
                        && !status.equalsIgnoreCase("REJECTED"))) {
            throw new RuntimeException(
                    "Status must be ACCEPTED or REJECTED");
        }

        MentorshipStatus newStatus =
                MentorshipStatus.valueOf(status.toUpperCase());

        if (newStatus == MentorshipStatus.ACCEPTED) {

            if (meetingLink == null || meetingLink.isBlank()) {
                throw new RuntimeException(
                        "Meeting link is required to accept a session");
            }

            session.setMeetingLink(meetingLink);
        }

        session.setStatus(newStatus);

        MentorshipSession updatedSession =
                sessionRepository.save(session);

        // Notify student
        String message = newStatus == MentorshipStatus.ACCEPTED
                ? "Your mentorship booking has been accepted. Meeting link: "
                + meetingLink
                : "Your mentorship booking has been rejected.";

        notificationService.createNotification(
                session.getStudent().getId(),
                "Mentorship Booking Update",
                message,
                "MENTORSHIP_STATUS"
        );

        return updatedSession;
    }

    // 5. Student cancels session
    @Override
    @Transactional
    public void cancelSession(
            Long sessionId,
            String studentEmail) {

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        MentorshipSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Session not found"));

        if (!session.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException(
                    "You cannot cancel another student's session");
        }

        if (session.getStatus() != MentorshipStatus.PENDING) {
            throw new RuntimeException(
                    "Only pending sessions can be cancelled");
        }

        session.setStatus(MentorshipStatus.CANCELLED);

        sessionRepository.save(session);

        // Notify instructor
        notificationService.createNotification(
                session.getInstructor().getId(),
                "Mentorship Session Cancelled",
                student.getName() + " cancelled the mentorship session: "
                        + session.getTopic(),
                "MENTORSHIP_CANCELLED"
        );
    }

    // 6. Instructor completes session
    @Override
    @Transactional
    public MentorshipSession completeSession(
            Long sessionId,
            String instructorEmail) {

        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() ->
                        new RuntimeException("Instructor not found"));

        MentorshipSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new RuntimeException("Mentorship session not found"));

        if (!session.getInstructor().getId().equals(instructor.getId())) {
            throw new RuntimeException(
                    "You cannot complete another instructor's session");
        }

        if (session.getStatus() != MentorshipStatus.ACCEPTED) {
            throw new RuntimeException(
                    "Only accepted sessions can be completed");
        }

        LocalDateTime sessionEnd = session.getScheduledAt()
                .plusMinutes(session.getDurationMinutes());

        if (LocalDateTime.now().isBefore(sessionEnd)) {
            throw new RuntimeException(
                    "You cannot complete a session before its scheduled end time");
        }

        session.setStatus(MentorshipStatus.COMPLETED);

        MentorshipSession completedSession =
                sessionRepository.save(session);

        // Notify student
        notificationService.createNotification(
                session.getStudent().getId(),
                "Mentorship Session Completed",
                "Your mentorship session on "
                        + session.getTopic()
                        + " has been marked as completed. You can now submit a review.",
                "MENTORSHIP_COMPLETED"
        );

        return completedSession;
    }
}
