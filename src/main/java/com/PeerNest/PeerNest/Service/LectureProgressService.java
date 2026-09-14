package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.ProgressResponse;

public interface LectureProgressService {

    void markLectureComplete(
            Long lectureId,
            String studentEmail
    );

    ProgressResponse getCourseProgress(
            Long courseId,
            String studentEmail
    );
}