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
}
