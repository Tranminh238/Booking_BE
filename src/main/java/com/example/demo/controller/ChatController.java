package com.example.demo.controller;

import com.example.demo.dto.Chat.*;
import com.example.demo.service.ConversationService;
import com.example.demo.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    /**
     * Tạo hoặc lấy conversation giữa user và hotel
     * POST /api/conversations/start
     */
    @PostMapping("/start")
    public ResponseEntity<?> startConversation(@RequestBody StartConversationRequest request) {
        try {
            ConversationDTO dto = conversationService.getOrCreateConversation(
                    request.getUserId(),
                    request.getHotelId()
            );
            return ResponseEntity.ok(buildResponse(200, "Thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(buildResponse(400, e.getMessage(), null));
        }
    }

    /**
     * Lấy danh sách conversation của khách hàng
     * GET /api/conversations/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserConversations(@PathVariable Long userId) {
        List<ConversationDTO> list = conversationService.getConversationsByUser(userId);
        return ResponseEntity.ok(buildResponse(200, "Thành công", list));
    }

    /**
     * Lấy danh sách conversation theo hotel
     * GET /api/conversations/hotel/{hotelId}
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getHotelConversations(@PathVariable Long hotelId) {
        List<ConversationDTO> list = conversationService.getConversationsByHotel(hotelId);
        return ResponseEntity.ok(buildResponse(200, "Thành công", list));
    }

    /**
     * Lấy tin nhắn của 1 conversation
     * GET /api/conversations/{id}/messages
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        List<MessageDTO> messages = messageService.getMessages(id);
        return ResponseEntity.ok(buildResponse(200, "Thành công", messages));
    }

    /**
     * Gửi tin nhắn qua REST (ngoài WebSocket)
     * POST /api/conversations/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            MessageDTO dto = messageService.sendMessage(request);
            return ResponseEntity.ok(buildResponse(200, "Gửi thành công", dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(buildResponse(400, e.getMessage(), null));
        }
    }

    /**
     * Đánh dấu đã đọc cho khách hàng
     * POST /api/conversations/{id}/read/customer
     */
    @PostMapping("/{id}/read/customer")
    public ResponseEntity<?> markReadAsCustomer(@PathVariable Long id) {
        conversationService.markReadAsCustomer(id);
        messageService.markMessagesAsRead(id);
        return ResponseEntity.ok(buildResponse(200, "Đã đánh dấu đã đọc", null));
    }

    /**
     * Đánh dấu đã đọc cho chủ khách sạn
     * POST /api/conversations/{id}/read/owner
     */
    @PostMapping("/{id}/read/owner")
    public ResponseEntity<?> markReadAsOwner(@PathVariable Long id) {
        conversationService.markReadAsOwner(id);
        messageService.markMessagesAsRead(id);
        return ResponseEntity.ok(buildResponse(200, "Đã đánh dấu đã đọc", null));
    }

    private Map<String, Object> buildResponse(int status, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
}
