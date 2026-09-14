package com.PeerNest.PeerNest.Repository;
import com.PeerNest.PeerNest.Entity.Lecture;
import com.PeerNest.PeerNest.Entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    List<Lecture> findBySectionIdOrderByLectureOrder(Long sectionId);
}
