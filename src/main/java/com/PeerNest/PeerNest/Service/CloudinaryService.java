package com.PeerNest.PeerNest.Service;

import com.PeerNest.PeerNest.Dto.CloudinaryUploadResult;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;


    // =========================================================
    // UPLOAD VIDEO
    // =========================================================

    public String uploadVideo(MultipartFile file) throws IOException {

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video",
                        "folder", "peernest/videos"
                )
        );

        return result.get("secure_url").toString();
    }


    // =========================================================
    // UPLOAD DOCUMENT
    // =========================================================

    public CloudinaryUploadResult uploadDocument(
            MultipartFile file) throws IOException {

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "peernest/resources"
                )
        );

        String url = result.get("secure_url").toString();

        String publicId = result.get("public_id").toString();

        return new CloudinaryUploadResult(
                url,
                publicId
        );
    }


    // =========================================================
    // DELETE DOCUMENT
    // =========================================================

    public void deleteDocument(String publicId) throws Exception {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap(
                        "resource_type", "raw"
                )
        );
    }
}