package com.PeerNest.PeerNest.Service;
import com.PeerNest.PeerNest.Dto.LectureRequest;
import com.PeerNest.PeerNest.Entity.Lecture;

import java.util.List;

public interface LectureService {

    Lecture createLecture(
            LectureRequest request,
            String instructorEmail
    );

    List<Lecture> getLectures(Long sectionId);

    void deleteLecture(
            Long lectureId,
            String instructorEmail
    );
}
