package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.repository.AmentityCategoryRepository;
import com.example.demo.dto.AmentityCategory.request.AmenityCategoryForm;
import com.example.demo.dto.AmentityCategory.response.AmenityCategoryResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.entity.AmenityCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmentityCategoryService {
    private final AmentityCategoryRepository amentityCategoryRepository;

    public BaseResponse createAmenityCategory(AmenityCategoryForm form) {

        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return new BaseResponse(400, "Name is required", null);
        }

        if (amentityCategoryRepository.existsByName(form.getName())) {
            return new BaseResponse(400, "Amenity category already exists", null);
        }

        AmenityCategory entity = AmenityCategory.builder()
                .name(form.getName())
                .build();

        amentityCategoryRepository.save(entity);

        return new BaseResponse(200, "Success", mapToResponse(entity));
    }

    public BaseResponse updateAmenityCategory(AmenityCategoryForm form) {

        AmenityCategory entity = amentityCategoryRepository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Amenity category not found"));

        entity.setName(form.getName());
        amentityCategoryRepository.save(entity);

        return new BaseResponse(200, "Success", mapToResponse(entity));
    }

    public BaseResponse deleteAmenityCategory(Long id) {

        AmenityCategory entity = amentityCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Amenity category not found"));

        amentityCategoryRepository.delete(entity);

        return new BaseResponse(200, "Success", null);
    }

    public BaseResponse getAllAmentityCategory() {
        List<AmenityCategoryResponse> result = amentityCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new BaseResponse(200, "Success", result);
    }

    private AmenityCategoryResponse mapToResponse(AmenityCategory entity) {
        return AmenityCategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
