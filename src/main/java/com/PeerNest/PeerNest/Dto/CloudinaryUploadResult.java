package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudinaryUploadResult {

    private String url;
    private String publicId;
}