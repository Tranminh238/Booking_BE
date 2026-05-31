package com.example.demo.dto.Chat;

import com.example.demo.enums.SenderRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private Long conversationId;
    private Long senderId;
    private SenderRole senderRole;
    private String content;
}
