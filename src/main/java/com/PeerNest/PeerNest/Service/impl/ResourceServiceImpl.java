package com.PeerNest.PeerNest.Service.impl;

import com.PeerNest.PeerNest.Dto.CloudinaryUploadResult;
import com.PeerNest.PeerNest.Dto.ResourceRequest;
import com.PeerNest.PeerNest.Dto.ResourceResponse;
import com.PeerNest.PeerNest.Entity.Course;
import com.PeerNest.PeerNest.Entity.Resource;
import com.PeerNest.PeerNest.Entity.Section;
import com.PeerNest.PeerNest.Entity.User;
import com.PeerNest.PeerNest.Repository.EnrollmentRepository;
import com.PeerNest.PeerNest.Repository.ResourceRepository;
import com.PeerNest.PeerNest.Repository.SectionRepository;
import com.PeerNest.PeerNest.Repository.UserRepository;
import com.PeerNest.PeerNest.Service.CloudinaryService;
import com.PeerNest.PeerNest.Service.ResourceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    // Maximum resource size = 10 MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


    // =========================================================
    // UPLOAD RESOURCE
    // =========================================================

    @Override
    public ResourceResponse uploadResource(
            ResourceRequest request,
            MultipartFile file,
            String instructorEmail) {

        // -----------------------------------------------------
        // 1. Validate file exists
        // -----------------------------------------------------

        validateFile(file);


        // -----------------------------------------------------
        // 2. Validate request
        // -----------------------------------------------------

        validateResourceRequest(request);


        // -----------------------------------------------------
        // 3. Find section
        // -----------------------------------------------------

        Section section = sectionRepository
                .findById(request.getSectionId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Section not found"
                ));


        // -----------------------------------------------------
        // 4. Find course
        // -----------------------------------------------------

        Course course = section.getCourse();

        if (course == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found for this section"
            );
        }


        // -----------------------------------------------------
        // 5. Find instructor
        // -----------------------------------------------------

        User instructor = userRepository
                .findByEmail(instructorEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Instructor not found"
                ));


        // -----------------------------------------------------
        // 6. Verify instructor ownership
        // -----------------------------------------------------

        if (course.getInstructor() == null ||
                !course.getInstructor()
                        .getId()
                        .equals(instructor.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only upload resources to your own course"
            );
        }


        // -----------------------------------------------------
        // 7. Upload to Cloudinary
        // -----------------------------------------------------

        CloudinaryUploadResult uploadResult;

        try {

            System.out.println("====================================");
            System.out.println("RESOURCE UPLOAD STARTED");
            System.out.println("Instructor: " + instructorEmail);
            System.out.println("File: " + file.getOriginalFilename());
            System.out.println("Size: " + file.getSize());
            System.out.println("Type: " + file.getContentType());

            System.out.println("Uploading file to Cloudinary...");

            uploadResult = cloudinaryService.uploadDocument(file);

            System.out.println("Cloudinary upload successful");
            System.out.println("URL: " + uploadResult.getUrl());
            System.out.println("Public ID: " + uploadResult.getPublicId());

        } catch (Exception e) {

            System.err.println("====================================");
            System.err.println("CLOUDINARY RESOURCE UPLOAD FAILED");
            System.err.println("Exception: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====================================");

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload resource: " + e.getMessage(),
                    e
            );
        }


        // -----------------------------------------------------
        // 8. Save resource
        // -----------------------------------------------------

        Resource resource = Resource.builder()
                .title(request.getTitle().trim())
                .description(
                        request.getDescription() == null
                                ? null
                                : request.getDescription().trim()
                )
                .fileUrl(uploadResult.getUrl())
                .publicId(uploadResult.getPublicId())
                .type(request.getType())
                .section(section)
                .uploadedAt(LocalDateTime.now())
                .build();


        Resource savedResource = resourceRepository.save(resource);


        System.out.println("Resource saved successfully");
        System.out.println("Resource ID: " + savedResource.getId());
        System.out.println("====================================");


        return mapToResponse(savedResource);
    }


    // =========================================================
    // FILE VALIDATION
    // =========================================================

    private void validateFile(MultipartFile file) {

        // File missing
        if (file == null || file.isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource file is required"
            );
        }


        // File size
        if (file.getSize() > MAX_FILE_SIZE) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File size cannot exceed 10 MB"
            );
        }


        // Content type
        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.equalsIgnoreCase("application/pdf")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed"
            );
        }


        // File extension
        String fileName = file.getOriginalFilename();

        if (fileName == null ||
                !fileName.toLowerCase().endsWith(".pdf")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PDF files are allowed"
            );
        }
    }


    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateResourceRequest(ResourceRequest request) {

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource request is required"
            );
        }


        if (request.getTitle() == null ||
                request.getTitle().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource title is required"
            );
        }


        if (request.getTitle().trim().length() > 200) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource title cannot exceed 200 characters"
            );
        }


        if (request.getDescription() != null &&
                request.getDescription().length() > 1000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource description cannot exceed 1000 characters"
            );
        }


        if (request.getType() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Resource type is required"
            );
        }
    }


    // =========================================================
    // GET INSTRUCTOR RESOURCES
    // =========================================================

    @Override
    public List<ResourceResponse> getInstructorResources(
            Long sectionId,
            String instructorEmail) {

        Section section = findSection(sectionId);

        verifyInstructorOwnership(
                section.getCourse(),
                instructorEmail
        );

        return resourceRepository
                .findBySectionIdOrderByUploadedAtDesc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // GET STUDENT RESOURCES
    // =========================================================

    @Override
    public List<ResourceResponse> getStudentResources(
            Long sectionId,
            String studentEmail) {

        Section section = findSection(sectionId);

        Course course = section.getCourse();

        if (course == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }


        User student = userRepository
                .findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"
                ));


        boolean enrolled =
                enrollmentRepository.existsByStudentIdAndCourseId(
                        student.getId(),
                        course.getId()
                );


        if (!enrolled) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not enrolled in this course"
            );
        }


        return resourceRepository
                .findBySectionIdOrderByUploadedAtDesc(sectionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // DELETE RESOURCE
    // =========================================================

    @Override
    public void deleteResource(
            Long resourceId,
            String instructorEmail) {

        Resource resource = resourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resource not found"
                ));


        verifyInstructorOwnership(
                resource.getSection().getCourse(),
                instructorEmail
        );


        // Delete from Cloudinary
        if (resource.getPublicId() != null &&
                !resource.getPublicId().isBlank()) {

            try {

                System.out.println(
                        "Deleting resource from Cloudinary: "
                                + resource.getPublicId()
                );

                cloudinaryService.deleteDocument(
                        resource.getPublicId()
                );

                System.out.println(
                        "Cloudinary resource deleted successfully"
                );

            } catch (Exception e) {

                e.printStackTrace();

                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete resource from Cloudinary",
                        e
                );
            }
        }


        // Delete database record
        resourceRepository.delete(resource);
    }


    // =========================================================
    // FIND SECTION
    // =========================================================

    private Section findSection(Long sectionId) {

        return sectionRepository
                .findById(sectionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Section not found"
                ));
    }


    // =========================================================
    // VERIFY INSTRUCTOR OWNERSHIP
    // =========================================================

    private void verifyInstructorOwnership(
            Course course,
            String instructorEmail) {

        if (course == null) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"
            );
        }


        if (course.getInstructor() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This course has no instructor assigned"
            );
        }


        if (course.getInstructor().getEmail() == null ||
                !course.getInstructor()
                        .getEmail()
                        .equalsIgnoreCase(instructorEmail)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access resources of your own course"
            );
        }
    }


    // =========================================================
    // MAP ENTITY TO RESPONSE
    // =========================================================

    private ResourceResponse mapToResponse(Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getFileUrl(),
                resource.getType(),
                resource.getSection().getId(),
                resource.getSection().getTitle(),
                resource.getUploadedAt()
        );
    }
}