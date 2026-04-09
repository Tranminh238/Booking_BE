package com.example.demo.dto.AmenityCategory.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmenityCategoryResponse {
    private Long id;
    private String name;
}
