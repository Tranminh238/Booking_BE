package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "promotion_rooms")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromtionRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long promotionId;
    private Long roomId;
    private Integer discountPrice;
    private Integer totalQuantity;
    private Integer quantityUsed;
}
