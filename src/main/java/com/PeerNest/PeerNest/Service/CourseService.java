package com.PeerNest.PeerNest.Service;
import com.PeerNest.PeerNest.Dto.CourseRequest;
import com.PeerNest.PeerNest.Dto.CourseResponse;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(
            CourseRequest request,
            String instructorEmail
    );

    List<CourseResponse> getInstructorCourses(
            String instructorEmail
    );

    CourseResponse getCourseById(Long id);

    CourseResponse updateCourse(
            Long id,
            CourseRequest request,
            String instructorEmail
    );

    void deleteCourse(
            Long id,
            String instructorEmail
    );
}
