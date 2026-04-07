package com.example.demo.dto.Room.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private Long id;
    private Long hotelId;
    private Long roomTypeId;
    private Integer pricePerNight;
    private Integer capacity;
    private Integer quantity;
    private Integer area;
    private Integer status;
    private String description;
    private List<Long> amenityIds;
    private List<String> imageUrls;
}
