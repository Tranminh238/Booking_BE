package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer totalPrice;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
