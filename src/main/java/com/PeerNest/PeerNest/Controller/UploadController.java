package com.PeerNest.PeerNest.Controller;

import com.PeerNest.PeerNest.Dto.VideoUploadResponse;
import com.PeerNest.PeerNest.Service.CloudinaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/instructor/upload")
@RequiredArgsConstructor
public class UploadController {


    private final CloudinaryService cloudinaryService;


    @PostMapping("/video")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file) {


        try {

            // ==========================================
            // CHECK FILE
            // ==========================================

            if (file == null || file.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("File is empty");
            }


            // ==========================================
            // CHECK CONTENT TYPE
            // ==========================================

            String contentType =
                    file.getContentType();


            if (
                    contentType == null ||
                            !contentType.startsWith("video/")
            ) {

                return ResponseEntity
                        .badRequest()
                        .body("Only video files are allowed");
            }


            System.out.println(
                    "Uploading video: " +
                            file.getOriginalFilename()
            );

            System.out.println(
                    "Content type: " +
                            contentType
            );

            System.out.println(
                    "File size: " +
                            file.getSize()
            );


            // ==========================================
            // CLOUDINARY
            // ==========================================

            String videoUrl =
                    cloudinaryService.uploadVideo(file);


            System.out.println(
                    "Cloudinary upload successful"
            );

            System.out.println(
                    "Video URL: " +
                            videoUrl
            );


            // ==========================================
            // SAFE RESPONSE
            // ==========================================

            VideoUploadResponse response =
                    new VideoUploadResponse(
                            "Video uploaded successfully",
                            videoUrl
                    );


            return ResponseEntity
                    .ok(response);


        } catch (Exception e) {


            // ==========================================
            // PRINT REAL ERROR
            // ==========================================

            System.err.println(
                    "===================================="
            );

            System.err.println(
                    "VIDEO UPLOAD ERROR"
            );

            System.err.println(
                    e.getClass().getName()
            );

            System.err.println(
                    e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "===================================="
            );


            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Video upload failed: " +
                                    e.getMessage()
                    );
        }
    }
}