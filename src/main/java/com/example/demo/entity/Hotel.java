package com.example.demo.entity;

import java.time.LocalDateTime;
import java.sql.Time;

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
    private Time checkin_time_start;
    private Time checkin_time_end;
    private Time checkout_time_start;
    private Time checkout_time_end;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
