package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.ProgressResponse;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.LectureProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LectureProgressServiceImpl
        implements LectureProgressService {

    private final LectureRepository lectureRepository;
    private final LectureProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Override
    public void markLectureComplete(
            Long lectureId,
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        Lecture lecture = lectureRepository
                .findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found"));

        Long courseId = lecture
                .getSection()
                .getCourse()
                .getId();

        // Check enrollment
        boolean enrolled =
                enrollmentRepository
                        .existsByStudentIdAndCourseId(
                                student.getId(),
                                courseId
                        );

        if (!enrolled) {
            throw new RuntimeException(
                    "You are not enrolled in this course"
            );
        }

        LectureProgress progress =
                progressRepository
                        .findByStudentIdAndLectureId(
                                student.getId(),
                                lectureId
                        )
                        .orElse(
                                LectureProgress.builder()
                                        .student(student)
                                        .lecture(lecture)
                                        .build()
                        );

        progress.setCompleted(true);
        progress.setCompletedAt(
                LocalDateTime.now()
        );

        progressRepository.save(progress);
    }

    @Override
    public ProgressResponse getCourseProgress(
            Long courseId,
            String studentEmail) {

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        boolean enrolled =
                enrollmentRepository
                        .existsByStudentIdAndCourseId(
                                student.getId(),
                                courseId
                        );

        if (!enrolled) {
            throw new RuntimeException(
                    "You are not enrolled in this course"
            );
        }

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        int totalLectures =
                course.getSections()
                        .stream()
                        .mapToInt(section ->
                                section.getLectures().size())
                        .sum();

        int completedLectures =
                (int) progressRepository
                        .countByStudentIdAndLectureSectionCourseIdAndCompleted(
                                student.getId(),
                                courseId,
                                true
                        );

        double percentage = totalLectures == 0
                ? 0
                : ((double) completedLectures
                / totalLectures) * 100;

        return new ProgressResponse(
                courseId,
                course.getTitle(),
                totalLectures,
                completedLectures,
                percentage
        );
    }
}