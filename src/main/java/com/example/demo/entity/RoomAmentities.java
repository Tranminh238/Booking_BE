package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "room_amenities")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomAmentities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Long amenityId;
}
