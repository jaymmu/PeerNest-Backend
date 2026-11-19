
package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.CloudinaryUploadResult;
import com.PeerNest.PeerNest.Entity.*;
import com.PeerNest.PeerNest.Repository.*;
import com.PeerNest.PeerNest.Service.CloudinaryService;
import com.PeerNest.PeerNest.Service.StudyResourceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyResourceServiceImpl
        implements StudyResourceService {

    private final StudyResourceRepository resourceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CloudinaryService cloudinaryService;

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    // UPLOAD NOTES OR PYQ
    @Override
    @Transactional
    public StudyResource uploadResource(
            Long courseId,
            String title,
            String description,
            ResourceType type,
            Integer semester,
            Integer year,
            MultipartFile file,
            String userEmail
    ) throws IOException {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        // Only instructor or admin can upload
        if (user.getRole() != Role.ADMIN) {

            if (user.getRole() != Role.INSTRUCTOR) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Only instructors and admins can upload resources");
            }

            if (!course.getInstructor().getId()
                    .equals(user.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You can upload resources only to your own courses");
            }
        }

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please select a PDF file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PDF size must not exceed 10 MB");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".pdf") ||
                !"application/pdf".equalsIgnoreCase(
                        file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed");
        }

        if (title == null || title.isBlank() || type == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title and resource type are required");
        }

        if (semester == null || semester < 1 || semester > 12) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Semester must be between 1 and 12");
        }

        if (type == ResourceType.PYQ &&
                (year == null || year < 1900 || year > 2100)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid year is required for PYQs");
        }

        if (type == ResourceType.NOTES) {
            year = null;
        }

        // Upload PDF to Cloudinary
        CloudinaryUploadResult uploadResult =
                cloudinaryService.uploadDocument(file);

        // Save resource metadata in MySQL
        StudyResource resource = StudyResource.builder()
                .title(title.trim())
                .description(description)
                .type(type)
                .semester(semester)
                .year(year)
                .fileUrl(uploadResult.getUrl())
                .publicId(uploadResult.getPublicId())
                .fileName(fileName)
                .course(course)
                .uploadedBy(user)
                .build();

        return resourceRepository.save(resource);
    }

    // GET COURSE RESOURCES
    @Override
    @Transactional(readOnly = true)
    public List<StudyResource> getCourseResources(
            Long courseId,
            String userEmail
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        // Admin can access all resources
        if (user.getRole() == Role.ADMIN) {
            return resourceRepository.findByCourseId(courseId);
        }

        // Instructor can access own course resources
        if (user.getRole() == Role.INSTRUCTOR) {

            if (!course.getInstructor().getId()
                    .equals(user.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot access another instructor's resources");
            }

            return resourceRepository.findByCourseId(courseId);
        }

        // Student must be enrolled
        boolean enrolled =
                enrollmentRepository.existsByStudentIdAndCourseId(
                        user.getId(), courseId);

        if (!enrolled) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not enrolled in this course");
        }

        return resourceRepository.findByCourseId(courseId);
    }

    // DELETE RESOURCE
    @Override
    @Transactional
    public void deleteResource(
            Long resourceId,
            String userEmail
    ) throws Exception {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        StudyResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resource not found"));

        // Only admin or uploading instructor can delete
        if (user.getRole() != Role.ADMIN) {

            if (user.getRole() != Role.INSTRUCTOR ||
                    !resource.getUploadedBy().getId()
                            .equals(user.getId())) {

                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You cannot delete this resource");
            }
        }

        // Delete document from Cloudinary
        cloudinaryService.deleteDocument(resource.getPublicId());

        // Delete metadata from MySQL
        resourceRepository.delete(resource);
    }
}
