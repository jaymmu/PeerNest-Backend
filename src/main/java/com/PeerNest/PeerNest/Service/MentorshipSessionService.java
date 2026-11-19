
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Entity.MentorshipSession;
import java.util.List;

public interface MentorshipSessionService {

    MentorshipSession bookSession(
            Long instructorId,
            String topic,
            String description,
            java.time.LocalDateTime scheduledAt,
            Integer durationMinutes,
            String studentEmail
    );

    List<MentorshipSession> getStudentSessions(String studentEmail);

    List<MentorshipSession> getInstructorSessions(String instructorEmail);

    MentorshipSession updateSessionStatus(
            Long sessionId,
            String status,
            String meetingLink,
            String instructorEmail
    );

    void cancelSession(Long sessionId, String studentEmail);
    MentorshipSession completeSession(
            Long sessionId,
            String instructorEmail
    );
}
