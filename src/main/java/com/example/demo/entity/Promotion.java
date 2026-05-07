package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "promotions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Integer discountPercentage;
    private Integer quantityRoom;
    private Integer quantityUsed;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
}
