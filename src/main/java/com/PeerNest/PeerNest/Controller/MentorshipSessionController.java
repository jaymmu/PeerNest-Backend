
package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.MentorshipSessionResponse;
import com.PeerNest.PeerNest.Entity.MentorshipSession;
import com.PeerNest.PeerNest.Service.MentorshipSessionService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mentorship")
@RequiredArgsConstructor
public class MentorshipSessionController {

    private final MentorshipSessionService mentorshipSessionService;

    // Student books a mentorship session
    @PostMapping("/student/book/{instructorId}")
    public ResponseEntity<MentorshipSessionResponse> bookSession(
            @PathVariable Long instructorId,
            @RequestParam String topic,
            @RequestParam(required = false) String description,
            @RequestParam LocalDateTime scheduledAt,
            @RequestParam Integer durationMinutes,
            Authentication authentication) {

        MentorshipSession session = mentorshipSessionService.bookSession(
                instructorId,
                topic,
                description,
                scheduledAt,
                durationMinutes,
                authentication.getName()
        );

        return ResponseEntity.ok(toResponse(session));
    }

    // Student views their sessions
    @GetMapping("/student/my-sessions")
    public ResponseEntity<List<MentorshipSessionResponse>> getStudentSessions(
            Authentication authentication) {

        List<MentorshipSessionResponse> sessions =
                mentorshipSessionService
                        .getStudentSessions(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(sessions);
    }

    // Instructor views their sessions
    @GetMapping("/instructor/my-sessions")
    public ResponseEntity<List<MentorshipSessionResponse>> getInstructorSessions(
            Authentication authentication) {

        List<MentorshipSessionResponse> sessions =
                mentorshipSessionService
                        .getInstructorSessions(authentication.getName())
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(sessions);
    }

    // Instructor accepts or rejects a session
    @PutMapping("/instructor/update/{sessionId}")
    public ResponseEntity<MentorshipSessionResponse> updateSession(
            @PathVariable Long sessionId,
            @RequestParam String status,
            @RequestParam(required = false) String meetingLink,
            Authentication authentication) {

        MentorshipSession session =
                mentorshipSessionService.updateSessionStatus(
                        sessionId,
                        status,
                        meetingLink,
                        authentication.getName()
                );

        return ResponseEntity.ok(toResponse(session));
    }

    // Student cancels a pending session
    @PutMapping("/student/cancel/{sessionId}")
    public ResponseEntity<String> cancelSession(
            @PathVariable Long sessionId,
            Authentication authentication) {

        mentorshipSessionService.cancelSession(
                sessionId,
                authentication.getName()
        );

        return ResponseEntity.ok("Mentorship session cancelled successfully");
    }

    private MentorshipSessionResponse toResponse(MentorshipSession session) {
        return MentorshipSessionResponse.builder()
                .id(session.getId())
                .topic(session.getTopic())
                .description(session.getDescription())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .meetingLink(session.getMeetingLink())
                .status(session.getStatus().name())
                .studentId(session.getStudent().getId())
                .studentName(session.getStudent().getName())
                .instructorId(session.getInstructor().getId())
                .instructorName(session.getInstructor().getName())
                .createdAt(session.getCreatedAt())
                .build();
    }
    // Instructor marks a mentorship session as completed
    @PutMapping("/instructor/complete/{sessionId}")
    public ResponseEntity<MentorshipSessionResponse> completeSession(
            @PathVariable Long sessionId,
            Authentication authentication) {

        MentorshipSession session =
                mentorshipSessionService.completeSession(
                        sessionId,
                        authentication.getName()
                );

        return ResponseEntity.ok(toResponse(session));
    }
}
