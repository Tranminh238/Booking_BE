package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "hotel_amenities")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAmentities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long hotelId;
    private Long amenityId;
}
