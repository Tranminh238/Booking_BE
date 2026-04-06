package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "hotel_addresses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hotelId;
    private String district;
    private String city;
    private String country;
}
