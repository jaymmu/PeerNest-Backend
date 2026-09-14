package com.PeerNest.PeerNest.Dto;
import com.PeerNest.PeerNest.Entity.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CourseRequest {

    @NotBlank(message = "Course title is required")
    private String title;

    @NotBlank(message = "Course description is required")
    private String description;

    @PositiveOrZero(message = "Price cannot be negative")
    private double price;

    private boolean free;

    @NotNull(message = "Course level is required")
    private CourseLevel level;

    @NotNull(message = "Subject is required")
    private Long subjectId;
}
