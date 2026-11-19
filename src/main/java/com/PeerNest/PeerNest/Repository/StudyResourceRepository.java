
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.StudyResource;
import com.PeerNest.PeerNest.Entity.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyResourceRepository
        extends JpaRepository<StudyResource, Long> {

    // Find all resources for a course
    List<StudyResource> findByCourseId(Long courseId);

    // Find resources by course and type (NOTES or PYQ)
    List<StudyResource> findByCourseIdAndType(
            Long courseId,
            ResourceType type
    );

    // Filter resources by semester
    List<StudyResource> findByCourseIdAndSemester(
            Long courseId,
            Integer semester
    );

    // Filter by course, type and semester
    List<StudyResource> findByCourseIdAndTypeAndSemester(
            Long courseId,
            ResourceType type,
            Integer semester
    );

    // Filter PYQs by year
    List<StudyResource> findByCourseIdAndTypeAndYear(
            Long courseId,
            ResourceType type,
            Integer year
    );
}
