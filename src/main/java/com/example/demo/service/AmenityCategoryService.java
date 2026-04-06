package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.repository.AmenityCategoryRepository;
import com.example.demo.dto.AmentityCategory.request.AmenityCategoryForm;
import com.example.demo.dto.AmentityCategory.response.AmenityCategoryResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.entity.AmenityCategory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmenityCategoryService {
    private final AmenityCategoryRepository amentityCategoryRepository;

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
