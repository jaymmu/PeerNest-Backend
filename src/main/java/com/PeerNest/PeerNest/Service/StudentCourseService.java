
package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.EnrollmentResponse;
import com.PeerNest.PeerNest.Dto.StudentCourseResponse;
import com.PeerNest.PeerNest.Entity.CourseLevel;

import java.util.List;

public interface StudentCourseService {

    List<StudentCourseResponse> getPublishedCourses();

    StudentCourseResponse getCourseById(Long courseId);

    List<StudentCourseResponse> searchCourses(String keyword);

    List<StudentCourseResponse> getCoursesBySubject(Long subjectId);

    List<StudentCourseResponse> getCoursesByLevel(CourseLevel level);

    List<StudentCourseResponse> getCoursesByFreeStatus(boolean free);

    EnrollmentResponse enroll(Long courseId, String studentEmail);

    List<EnrollmentResponse> getMyEnrollments(String studentEmail);
}
