package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "Hotel")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String name;
    private Integer star;
    private Float rating_avg;
    private Integer status;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
