package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.SectionRequest;
import com.PeerNest.PeerNest.Entity.Section;

import java.util.List;

public interface SectionService {

    Section createSection(
            SectionRequest request,
            String instructorEmail
    );

    List<Section> getSections(Long courseId);

    void deleteSection(
            Long sectionId,
            String instructorEmail
    );
}
