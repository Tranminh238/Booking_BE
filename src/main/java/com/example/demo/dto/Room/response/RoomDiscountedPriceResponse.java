package com.example.demo.dto.Room.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDiscountedPriceResponse {
    private Long roomId;
    private Integer originalPricePerNight;
    private Integer discountedPricePerNight;
    private Integer originalTotalPrice;
    private Double discountedTotalPrice;
    private Integer discountPercentage;
    private Boolean hasDiscount;
    private Long numberOfNights;
}
