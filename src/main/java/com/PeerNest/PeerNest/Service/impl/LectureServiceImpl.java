package com.PeerNest.PeerNest.Service.impl;
import com.PeerNest.PeerNest.Dto.LectureRequest;
import com.PeerNest.PeerNest.Entity.Lecture;
import com.PeerNest.PeerNest.Entity.Section;
import com.PeerNest.PeerNest.Repository.LectureRepository;
import com.PeerNest.PeerNest.Repository.SectionRepository;
import com.PeerNest.PeerNest.Service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepository lectureRepository;
    private final SectionRepository sectionRepository;

    @Override
    public Lecture createLecture(
            LectureRequest request,
            String instructorEmail) {

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() ->
                        new RuntimeException("Section not found"));

        if (!section.getCourse().getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only add lectures to your own courses");
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

    @Override
    public List<Lecture> getLectures(Long sectionId) {

        return lectureRepository
                .findBySectionIdOrderByLectureOrder(sectionId);
    }

    @Override
    public void deleteLecture(
            Long lectureId,
            String instructorEmail) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found"));

        if (!lecture.getSection().getCourse().getInstructor().getEmail()
                .equals(instructorEmail)) {

            throw new RuntimeException(
                    "You can only delete your own lectures");
        }

        lectureRepository.delete(lecture);
    }
}