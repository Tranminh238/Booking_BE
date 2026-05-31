package com.example.demo.entity;

import com.example.demo.enums.SenderRole;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long hotelId;

    @Enumerated(EnumType.STRING)
    private SenderRole initiatedBy;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    private LocalDateTime lastMessageAt;

    @Builder.Default
    private Integer unreadCountCustomer = 0;

    @Builder.Default
    private Integer unreadCountOwner = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
