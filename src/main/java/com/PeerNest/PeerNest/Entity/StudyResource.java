
package com.PeerNest.PeerNest.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    private String fileUrl;

    private String publicId;

    private String fileName;

    private Integer semester;

    private Integer year;

    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @PrePersist
    public void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
