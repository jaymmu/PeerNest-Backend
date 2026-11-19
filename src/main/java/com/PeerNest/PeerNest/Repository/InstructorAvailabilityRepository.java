
package com.PeerNest.PeerNest.Repository;

import com.PeerNest.PeerNest.Entity.InstructorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstructorAvailabilityRepository
        extends JpaRepository<InstructorAvailability, Long> {

    List<InstructorAvailability> findByInstructorId(Long instructorId);

    List<InstructorAvailability> findByInstructorIdAndAvailableTrue(
            Long instructorId
    );
}
