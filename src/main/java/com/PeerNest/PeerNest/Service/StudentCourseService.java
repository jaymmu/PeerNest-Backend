package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;

import java.util.List;

public interface StudentCourseService {

    List<StudentCourseResponse> getPublishedCourses();

    StudentCourseResponse getCourseById(Long courseId);

    List<StudentCourseResponse> searchCourses(String keyword);

    EnrollmentResponse enroll(
            Long courseId,
            String studentEmail
    );

    List<EnrollmentResponse> getMyEnrollments(
            String studentEmail
    );
}