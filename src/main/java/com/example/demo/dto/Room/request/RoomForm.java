package com.example.demo.dto.Room.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomForm {
    private Long id;
    private Long hotelId;
    private Long roomTypeId;
    private Integer pricePerNight;
    private Integer capacity;
    private Integer quantity;
    private Integer area;
    private Integer status;
    private String description;
    private LocalDateTime createdAt;
    private List<Long> amenityIds;
    private List<String> imageUrls;
}
