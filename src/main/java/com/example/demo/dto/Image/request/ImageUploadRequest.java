package com.example.demo.dto.Image.request;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUploadRequest {
    private Long refId;
    private String refType;
    private MultipartFile file;
}
