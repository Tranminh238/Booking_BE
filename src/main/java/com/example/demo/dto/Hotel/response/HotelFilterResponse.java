package com.example.demo.dto.Hotel.response;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Time;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFilterResponse {
    private Long id;
    private String name;
    private Integer star;
    private Float rating_avg;
    private Integer status;
    private String description;
    private String address;
    private String district;
    private String city;
    private String country;
    private Integer num_guest;
    private Time checkin_time_start;
    private Time checkin_time_end;
    private Time checkout_time_start;
    private Time checkout_time_end;
    private List<String> images;
    private List<String> amenities;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
    // Additional fields for filtering
    private String roomTypeName;
    private Double roomPricePerNight;
}
