package com.example.demo.dto.Chat;

import com.example.demo.enums.SenderRole;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private SenderRole senderRole;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
