package com.example.demo.dto.Amenity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmenityResponse {
    private Long id;
    private String name;
    private String type;
}
