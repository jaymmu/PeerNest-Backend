package com.PeerNest.PeerNest.Service.impl;
import com.PeerNest.PeerNest.Dto.SectionRequest;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.Section;
import com.PeerNest.PeerNest.Repository.CourseRepository;
import com.PeerNest.PeerNest.Repository.SectionRepository;
import com.PeerNest.PeerNest.Service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    @Override
    public Section createSection(
            SectionRequest request,
            String instructorEmail) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only add sections to your own courses");
        }

        Section section = Section.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .sectionOrder(request.getSectionOrder())
                .course(course)
                .build();

        return sectionRepository.save(section);
    }

    @Override
    public List<Section> getSections(Long courseId) {

        return sectionRepository
                .findByCourseIdOrderBySectionOrder(courseId);
    }

    @Override
    public void deleteSection(
            Long sectionId,
            String instructorEmail) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new RuntimeException("Section not found"));

        if (!section.getCourse().getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only delete your own sections");
        }

        sectionRepository.delete(section);
    }
}
