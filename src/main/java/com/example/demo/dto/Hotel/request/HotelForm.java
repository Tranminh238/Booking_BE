package com.example.demo.dto.Hotel.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.sql.Time;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelForm {
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String name;
    @NotNull
    private Integer star;
    @NotNull
    private Integer status;
    @NotNull
    private String checkin_time_start;
    @NotNull
    private String checkin_time_end;
    @NotNull
    private String checkout_time_start;
    @NotNull
    private String checkout_time_end;
    
    private String description;
    @NotNull
    private List<String> policy_url;
    
    private List<Long> amenityIds;
    @NotNull
    private List<String> images;
    @NotNull
    private String district;
    @NotNull
    private String city;
    @NotNull
    private String country;
}
