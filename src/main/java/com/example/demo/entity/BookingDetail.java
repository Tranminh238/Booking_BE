package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking_details")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bookingId;
    private Long roomId;
    private Integer numRoom;
    private Integer pricePerNight;
    private Integer numAdults;
    private Integer numChildren;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
