package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.LectureRequest;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.Lecture;
import com.PeerNest.PeerNest.Entity.Section;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.LectureRepository;
import com.PeerNest.PeerNest.Repository.SectionRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.LectureService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;


    // =========================================================
    // CREATE LECTURE
    // =========================================================

    @Override
    public Lecture createLecture(
            LectureRequest request,
            String instructorEmail) {

        Section section = sectionRepository
                .findById(request.getSectionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Section not found"
                ));


        if (section.getCourse() == null ||
                section.getCourse().getInstructor() == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course or instructor not found"
            );
        }


        if (!section.getCourse()
                .getInstructor()
                .getEmail()
                .equalsIgnoreCase(instructorEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only add lectures to your own courses"
            );
        }


        Lecture lecture = Lecture.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .videoUrl(request.getVideoUrl())
                .durationInMinutes(request.getDurationInMinutes())
                .lectureOrder(request.getLectureOrder())
                .freePreview(request.isFreePreview())
                .section(section)
                .build();


        return lectureRepository.save(lecture);
    }


    // =========================================================
    // GET LECTURES - INSTRUCTOR
    // =========================================================

    @Override
    public List<Lecture> getLectures(Long sectionId) {

        return lectureRepository
                .findBySectionIdOrderByLectureOrder(sectionId);
    }


    // =========================================================
    // DELETE LECTURE
    // =========================================================

    @Override
    public void deleteLecture(
            Long lectureId,
            String instructorEmail) {

        Lecture lecture = lectureRepository
                .findById(lectureId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lecture not found"
                ));


        if (lecture.getSection() == null ||
                lecture.getSection().getCourse() == null ||
                lecture.getSection().getCourse().getInstructor() == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course or instructor not found"
            );
        }


        if (!lecture.getSection()
                .getCourse()
                .getInstructor()
                .getEmail()
                .equalsIgnoreCase(instructorEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own lectures"
            );
        }


        lectureRepository.delete(lecture);
    }


    // =========================================================
    // GET LECTURES - STUDENT
    // =========================================================

    @Override
    public List<Lecture> getStudentLectures(
            Long sectionId,
            String studentEmail) {

        // -----------------------------------------------------
        // Find section
        // -----------------------------------------------------

        Section section = sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Section not found"
                ));


        // -----------------------------------------------------
        // Find course
        // -----------------------------------------------------

        Course course = section.getCourse();

        if (course == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }


        // -----------------------------------------------------
        // Find student
        // -----------------------------------------------------

        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"
                ));


        // -----------------------------------------------------
        // Check enrollment
        // -----------------------------------------------------

        boolean enrolled =
                enrollmentRepository.existsByStudentIdAndCourseId(
                        student.getId(),
                        course.getId()
                );


        // -----------------------------------------------------
        // Get all lectures
        // -----------------------------------------------------

        List<Lecture> lectures =
                lectureRepository
                        .findBySectionIdOrderByLectureOrder(sectionId);


        // =====================================================
        // ENROLLED STUDENT
        // =====================================================

        if (enrolled) {

            // Enrolled students can access everything.
            return lectures;
        }


        // =====================================================
        // NOT ENROLLED STUDENT
        // =====================================================

        /*
         * For an unenrolled student:
         *
         * Only lectures marked freePreview=true
         * can be returned.
         */

        List<Lecture> previewLectures = lectures
                .stream()
                .filter(Lecture::isFreePreview)
                .toList();


        // -----------------------------------------------------
        // No preview lectures
        // -----------------------------------------------------

        if (previewLectures.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not enrolled in this course"
            );
        }


        // -----------------------------------------------------
        // Return preview lectures only
        // -----------------------------------------------------

        return previewLectures;
    }
}