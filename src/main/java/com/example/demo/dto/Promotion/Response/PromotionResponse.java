package com.example.demo.dto.Promotion.Response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PromotionResponse {
    private Long id;
    private Long roomId;
    private int discountPercentage;
    private int priceAfterDiscount;
    private LocalDate startDate;
    private LocalDate endDate;
    private int quantityRoom;
    private int quantityUsed;
}
