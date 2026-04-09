package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.repository.AmenityRepository;
import com.example.demo.dto.Amenity.response.AmenityResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.entity.Amenity;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public BaseResponse getAllAmenity() {
        List<AmenityResponse> result = amenityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return new BaseResponse(200, "Success", result);
    }

    public BaseResponse getAmenityByCategoryId(Long categoryId) {
        List<AmenityResponse> result = amenityRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
        return new BaseResponse(200, "Success", result);
    }

    private AmenityResponse mapToResponse(Amenity entity) {
        return AmenityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .categoryId(entity.getCategoryId())
                .build();
    }
}
