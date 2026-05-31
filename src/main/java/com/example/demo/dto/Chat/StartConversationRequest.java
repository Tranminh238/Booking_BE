package com.example.demo.dto.Chat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartConversationRequest {
    private Long userId;
    private Long hotelId;
}
