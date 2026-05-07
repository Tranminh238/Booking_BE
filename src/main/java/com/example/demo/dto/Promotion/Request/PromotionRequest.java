package com.example.demo.dto.Promotion.Request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequest {
    private Long roomId;
    @Min(1) @Max(100)
    private Integer discountPercentage;
    private Integer quantityRoom;
    private LocalDate startDate;
    private LocalDate endDate;
}
