package com.example.demo.controller;

import com.example.demo.dto.Amenity.response.AmenityResponse;
import com.example.demo.service.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public ResponseEntity<List<AmenityResponse>> getAllAmenities(@RequestParam(required = false) String type) {
        List<AmenityResponse> amenities = amenityService.getAmenityByType(type);
        return ResponseEntity.ok(amenities);
    }
}
