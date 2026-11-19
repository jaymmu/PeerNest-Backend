
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.StudentDashboardResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.StudentDashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentDashboardServiceImpl
        implements StudentDashboardService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureProgressRepository progressRepository;
    private final CourseCertificateRepository certificateRepository;
    private final MentorshipSessionRepository mentorshipRepository;

    @Override
    public StudentDashboardResponse getDashboard(String email) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException(
                    "Only students can access this dashboard");
        }

        Long studentId = student.getId();

        // Enrolled courses
        List<Enrollment> enrollments =
                enrollmentRepository
                        .findAllByStudentIdOrderByEnrolledAtDesc(studentId);

        long totalEnrolledCourses = enrollments.size();

        // Course-wise progress
        List<StudentDashboardResponse.CourseProgress> courseProgress =
                enrollments.stream().map(enrollment -> {

                    Course course = enrollment.getCourse();

                    long totalLectures =
                            progressRepository
                                    .countTotalLecturesByCourse(course.getId());

                    long completedLectures =
                            progressRepository
                                    .countCompletedLectures(
                                            studentId, course.getId());

                    double percentage = totalLectures == 0
                            ? 0.0
                            : Math.round(
                            (completedLectures * 100.0
                                    / totalLectures) * 100.0
                    ) / 100.0;

                    return new StudentDashboardResponse.CourseProgress(
                            course.getId(),
                            course.getTitle(),
                            course.getThumbnailUrl(),
                            enrollment.getEnrolledAt(),
                            completedLectures,
                            totalLectures,
                            percentage
                    );
                }).toList();

        long totalLectures = courseProgress.stream()
                .mapToLong(
                        StudentDashboardResponse.CourseProgress::totalLectures)
                .sum();

        long totalCompletedLectures = courseProgress.stream()
                .mapToLong(
                        StudentDashboardResponse.CourseProgress::completedLectures)
                .sum();

        double overallProgress = totalLectures == 0
                ? 0.0
                : Math.round(
                (totalCompletedLectures * 100.0 / totalLectures)
                        * 100.0
        ) / 100.0;

        // Certificates
        List<StudentDashboardResponse.CertificateInfo> certificates =
                certificateRepository
                        .findAllByStudentIdOrderByIssueDateDesc(studentId)
                        .stream()
                        .map(certificate ->
                                new StudentDashboardResponse.CertificateInfo(
                                        certificate.getCertificateNumber(),
                                        certificate.getCourse().getTitle(),
                                        certificate.getIssueDate()
                                ))
                        .toList();

        // Upcoming mentorship sessions
        List<StudentDashboardResponse.MentorshipInfo> mentorships =
                mentorshipRepository.findUpcomingSessions(
                                studentId, LocalDateTime.now())
                        .stream()
                        .map(session ->
                                new StudentDashboardResponse.MentorshipInfo(
                                        session.getId(),
                                        session.getTopic(),
                                        session.getInstructor().getName(),
                                        session.getScheduledAt(),
                                        session.getDurationMinutes(),
                                        session.getMeetingLink(),
                                        session.getStatus().name()
                                ))
                        .toList();

        return new StudentDashboardResponse(
                student.getName(),
                totalEnrolledCourses,
                totalCompletedLectures,
                totalLectures,
                overallProgress,
                courseProgress,
                certificates,
                mentorships
        );
    }
}
