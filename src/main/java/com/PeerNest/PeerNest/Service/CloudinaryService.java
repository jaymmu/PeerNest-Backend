package com.PeerNest.PeerNest.Service;

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

    public String uploadVideo(MultipartFile file)
            throws IOException {

        Map<?, ?> result = cloudinary.uploader()
                .upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", "video",
                                "folder", "peernest/videos"
                        )
                );

        return result
                .get("secure_url")
                .toString();
    }
}