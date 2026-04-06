package com.example.demo.dto.Hotel.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelForm {
    private Long id;
    private String name;
    private Integer star;
    private Integer status;
    private String description;
    private List<Long> amenityIds;
    private List<String> imageUrls;
    private String district;
    private String city;
    private String country;
}
