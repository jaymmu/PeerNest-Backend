
package com.PeerNest.PeerNest.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalInstructors;

    private long totalCourses;
    private long totalEnrollments;

    private long totalMentorshipSessions;
    private long pendingSessions;
    private long acceptedSessions;
    private long rejectedSessions;
    private long completedSessions;
    private long cancelledSessions;
}
