
package com.PeerNest.PeerNest.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StudentDashboardResponse(
        String studentName,
        long totalEnrolledCourses,
        long totalCompletedLectures,
        long totalLectures,
        double overallProgress,
        List<CourseProgress> courses,
        List<CertificateInfo> certificates,
        List<MentorshipInfo> upcomingMentorships
) {
    public record CourseProgress(
            Long courseId,
            String courseTitle,
            String thumbnailUrl,
            LocalDateTime enrolledAt,
            long completedLectures,
            long totalLectures,
            double progressPercentage
    ) {}

    public record CertificateInfo(
            String certificateNumber,
            String courseTitle,
            LocalDate issueDate
    ) {}

    public record MentorshipInfo(
            Long sessionId,
            String topic,
            String instructorName,
            LocalDateTime scheduledAt,
            Integer durationMinutes,
            String meetingLink,
            String status
    ) {}
}
