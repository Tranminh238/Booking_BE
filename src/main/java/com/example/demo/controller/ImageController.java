package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.demo.service.ImageService;
import com.example.demo.entity.Image;
import com.example.demo.enums.ImageEmun;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.repository.ImageRepository;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService fileUploadService;
    private final ImageRepository imageRepository;

    @PostMapping("/upload")
    public String uploadImage(
            @RequestParam Long refId,
            @RequestParam String refType,
            @RequestParam("file") MultipartFile file
    ) {
        // 1. Upload file
        String imageUrl = fileUploadService.uploadImage(file);

        // 2. Lưu DB
        Image image = Image.builder()
                .refId(refId)
                .refType(RefType.valueOf(refType.toUpperCase()))
                .imageUrl(imageUrl)
                .build();

        imageRepository.save(image);

        return imageUrl;
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(fileUploadService.getUploadDir()).resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}