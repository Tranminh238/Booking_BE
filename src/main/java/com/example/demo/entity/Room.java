package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer pricePerNight;
    private Integer capacity;
    private Integer quantity;
    private Integer area;
    private Integer status;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
