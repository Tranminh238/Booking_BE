package com.example.demo.dto.Hotel.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {
    private Long id;
    private String name;
    private Integer star;
    private Float rating_avg;
    private Integer status;
    private String description;
    private String address;
    private List<String> images;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
