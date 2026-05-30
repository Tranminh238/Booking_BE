package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hotel_policy")
@Builder
public class HotelPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hotelId;
    private String identificationDocuments;
    private String checkInInstructions;
    private String smokePolicy;
    private String petPolicy;
    private Integer isRefund;
    private Integer minDateRefund; //ngày muộn nhất có thể hoàn tiền (tính từ ngày nhận phòng)
    private Integer refundPercentage; //tỷ lệ hoàn tiền 100% hoặc 0%
}
