package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.service.HotelDataLoaderService;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final HotelDataLoaderService hotelDataLoaderService;

    public ChatbotController(HotelDataLoaderService hotelDataLoaderService) {
        this.hotelDataLoaderService = hotelDataLoaderService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        return hotelDataLoaderService.chat(chatRequest);
    }

    @DeleteMapping("/history/{sessionId}")
    public String clearHistory(@PathVariable String sessionId) {
        hotelDataLoaderService.clearHistory(sessionId);
        return "Đã xóa lịch sử hội thoại";
    }
}
