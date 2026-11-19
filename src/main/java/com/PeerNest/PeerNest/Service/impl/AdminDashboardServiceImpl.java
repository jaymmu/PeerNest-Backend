
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.AdminDashboardResponse;
import com.PeerNest.PeerNest.Entity.MentorshipStatus;
import com.PeerNest.PeerNest.Entity.Role;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.MentorshipSessionRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.AdminDashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;

    @Override
    public AdminDashboardResponse getDashboardStatistics() {

        long totalUsers = userRepository.count();

        long totalStudents =
                userRepository.countByRole(Role.STUDENT);

        long totalInstructors =
                userRepository.countByRole(Role.INSTRUCTOR);

        long totalCourses = courseRepository.count();

        long totalEnrollments = enrollmentRepository.count();

        long totalMentorshipSessions =
                mentorshipSessionRepository.count();

        long pendingSessions =
                mentorshipSessionRepository.countByStatus(
                        MentorshipStatus.PENDING);

        long acceptedSessions =
                mentorshipSessionRepository.countByStatus(
                        MentorshipStatus.ACCEPTED);

        long rejectedSessions =
                mentorshipSessionRepository.countByStatus(
                        MentorshipStatus.REJECTED);

        long completedSessions =
                mentorshipSessionRepository.countByStatus(
                        MentorshipStatus.COMPLETED);

        long cancelledSessions =
                mentorshipSessionRepository.countByStatus(
                        MentorshipStatus.CANCELLED);

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalStudents(totalStudents)
                .totalInstructors(totalInstructors)
                .totalCourses(totalCourses)
                .totalEnrollments(totalEnrollments)
                .totalMentorshipSessions(totalMentorshipSessions)
                .pendingSessions(pendingSessions)
                .acceptedSessions(acceptedSessions)
                .rejectedSessions(rejectedSessions)
                .completedSessions(completedSessions)
                .cancelledSessions(cancelledSessions)
                .build();
    }
}
