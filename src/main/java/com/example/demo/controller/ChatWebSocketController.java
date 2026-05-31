package com.example.demo.controller;

import com.example.demo.dto.Chat.MessageDTO;
import com.example.demo.dto.Chat.SendMessageRequest;
import com.example.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;

    /**
     * Xử lý tin nhắn gửi qua WebSocket STOMP
     * Client gửi tới: /app/chat.send
     * Server broadcast tới: /topic/conversation/{conversationId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequest request) {
        // messageService sẽ tự broadcast qua SimpMessagingTemplate
        messageService.sendMessage(request);
    }
}
