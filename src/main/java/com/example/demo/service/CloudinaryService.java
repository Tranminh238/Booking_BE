package com.example.demo.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.CloudinaryResponse;

import jakarta.transaction.Transactional;

import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CloudinaryResponse uploadFile(MultipartFile file, String fileName) {
        try {
            final Map result = cloudinary.uploader().upload(file.getBytes(), Map.of("public_id", "nhndev/" + fileName));
            return new CloudinaryResponse(
                (String) result.get("secure_url"),
                (String) result.get("public_id")
            );
        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage(), e);
        }
    }
}
