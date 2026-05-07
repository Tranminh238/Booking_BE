package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.Promotion.Request.PromotionRequest;
import com.example.demo.dto.Promotion.Response.PromotionResponse;
import com.example.demo.entity.Promotion;
import com.example.demo.service.PromotionService;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping("/create")
    public ResponseEntity<Promotion> createPromotion(@RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id,
            @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.updatePromotion(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Promotion>> getPromotionsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(promotionService.getPromotionsByRoomId(roomId));
    }

    @GetMapping("/room/{roomId}/best")
    public ResponseEntity<Promotion> getBestPromotion(@PathVariable Long roomId) {
        return ResponseEntity.ok(promotionService.getBestPromotion(roomId).orElse(null));
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/valid")
    public ResponseEntity<List<PromotionResponse>> getValidPromotions() {
        List<Promotion> promotions = promotionService.getValidPromotions();
        List<PromotionResponse> responses = promotions.stream()
                .map(promotionService::mapToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

}
