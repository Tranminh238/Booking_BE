package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "room_availabilities")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private LocalDateTime date;
    private Integer quantity_available;
}
