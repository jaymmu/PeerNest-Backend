package com.PeerNest.PeerNest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoUploadResponse {

    private String message;

    private String url;
}