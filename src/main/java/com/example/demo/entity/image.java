package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long refId;
    private String refType;
    private String imageUrl;
    private LocalDateTime createAt;
}
