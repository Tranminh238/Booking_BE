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

    public List<AmenityResponse> getAmenityByType(String type) {
        List<Amenity> list;
        if (type == null || type.trim().isEmpty()) {
            list = amenityRepository.findAll();
        } else {
            list = amenityRepository.findByType(type);
        }
        return list.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AmenityResponse mapToResponse(Amenity entity) {
        return AmenityResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .build();
    }
}
