package com.PeerNest.PeerNest.Dto;
import com.PeerNest.PeerNest.Entity.CourseLevel;
import com.PeerNest.PeerNest.Entity.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private double price;
    private boolean free;
    private CourseLevel level;
    private CourseStatus status;
    private String subject;
    private String instructor;
}
