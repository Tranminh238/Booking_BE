package com.example.demo.dto.Chat;

import com.example.demo.enums.SenderRole;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private Long userId;
    private String userName;       // tên khách hàng
    private Long hotelId;
    private String hotelName;      // tên khách sạn
    private SenderRole initiatedBy;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCountCustomer;
    private Integer unreadCountOwner;
    private LocalDateTime createdAt;
}
