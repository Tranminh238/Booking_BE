package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.example.demo.enums.ImageEmun.RefType;

@Data
@Entity
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long refId;
    @Enumerated(EnumType.STRING)
    private RefType refType;
    private String imageUrl;
    private LocalDateTime createAt;
}
