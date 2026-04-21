package com.example.demo.dto.Hotel.request;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelFilter {
    private Integer star;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String city;
    private Integer minPrice;
    private Integer maxPrice;
    private List<String> amenities;
    private String roomType;
    private Integer avgRating;
    private String name;
    private String sort;
    private String order;
    private Integer page;
    private Integer size;
}
