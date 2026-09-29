package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.LectureRequest;
import com.PeerNest.PeerNest.Entity.Lecture;

import java.util.List;

public interface LectureService {

    Lecture createLecture(
            LectureRequest request,
            String instructorEmail
    );

    // For instructor usage
    List<Lecture> getLectures(Long sectionId);

    // For student usage - enrollment checked
    List<Lecture> getStudentLectures(
            Long sectionId,
            String studentEmail
    );

    void deleteLecture(
            Long lectureId,
            String instructorEmail
    );
}