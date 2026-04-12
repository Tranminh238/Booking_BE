// package com.example.demo.service;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.*;
// import java.util.List;
// import java.util.UUID;

// @Service
// public class ImageService {

//     @Value("${file.upload.directory:uploads}")
//     private String uploadDir;

//     @Value("${app.domain:http://localhost:8889}")
//     private String domain;

//     public String getUploadDir() {
//         return uploadDir;
//     }

//     public String uploadImage(MultipartFile file) {

//         try {
//             // 1. Validate file rỗng
//             if (file.isEmpty()) {
//                 throw new IllegalArgumentException("File trống");
//             }

//             // 2. Validate loại file
//             String contentType = file.getContentType();
//             List<String> allowedTypes = List.of(
//                     "image/jpeg",
//                     "image/png",
//                     "image/webp"
//             );

//             if (contentType == null || !allowedTypes.contains(contentType)) {
//                 throw new IllegalArgumentException("Chỉ cho phép JPG, PNG, WEBP");
//             }

//             // 3. Validate size (2MB)
//             if (file.getSize() > 2 * 1024 * 1024) {
//                 throw new IllegalArgumentException("File phải nhỏ hơn 2MB");
//             }

//             // 4. Tạo thư mục nếu chưa có
//             Path uploadPath = Paths.get(uploadDir);
//             if (!Files.exists(uploadPath)) {
//                 Files.createDirectories(uploadPath);
//             }

//             // 5. Tạo filename
//             String originalFilename = file.getOriginalFilename();
//             if (originalFilename == null || !originalFilename.contains(".")) {
//                 throw new IllegalArgumentException("Tên file không hợp lệ");
//             }

//             String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//             String filename = UUID.randomUUID() + extension;

//             // 6. Lưu file
//             Path filePath = uploadPath.resolve(filename);
//             Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

//             // 7. Trả URL
//             return domain.replaceAll("/$", "") + "/api/images/" + filename;

//         } catch (IOException e) {
//             throw new RuntimeException("Upload failed: " + e.getMessage(), e);
//         }
//     }
// }