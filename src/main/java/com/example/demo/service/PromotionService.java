package com.example.demo.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Promotion.Request.PromotionRequest;
import com.example.demo.dto.Promotion.Response.PromotionResponse;
import com.example.demo.entity.Promotion;
import com.example.demo.repository.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public Promotion createPromotion(PromotionRequest request) {
        if (request.getDiscountPercentage() > 100) {
            throw new RuntimeException("Discount must <= 100%");
        }
        if (request.getDiscountPercentage() <= 1) {
            throw new RuntimeException("Discount must be >= 1%");
        }

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new RuntimeException("startDate must be before endDate");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("startDate must be in the future");
        }

        Promotion p = new Promotion();
        p.setRoomId(request.getRoomId());
        p.setDiscountPercentage(request.getDiscountPercentage());
        p.setStartDate(request.getStartDate());
        p.setEndDate(request.getEndDate());
        p.setQuantityRoom(request.getQuantityRoom());
        p.setQuantityUsed(0);
        p.setStatus(1);

        return promotionRepository.save(p);
    }

    public Promotion updatePromotion(Long id, PromotionRequest request) {
        Promotion p = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
        if (request.getDiscountPercentage() > 100) {
            throw new RuntimeException("Discount must <= 100%");
        }
        if (request.getDiscountPercentage() <= 1) {
            throw new RuntimeException("Discount must be >= 1%");
        }

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new RuntimeException("startDate must be before endDate");
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("startDate must be in the future");
        }

        p.setDiscountPercentage(request.getDiscountPercentage());
        p.setStartDate(request.getStartDate());
        p.setEndDate(request.getEndDate());
        p.setQuantityRoom(request.getQuantityRoom());

        return promotionRepository.save(p);
    }

    public void deletePromotion(Long id) {
        Promotion p = promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Promotion not found"));
        p.setStatus(2);
        promotionRepository.save(p);
    }
    public Optional<Promotion> getBestPromotionForDate(Long roomId, LocalDate date) {
        return promotionRepository.findActivePromotionsForRoomAndDate(roomId, date)
                .stream()
                .max(Comparator.comparingInt(Promotion::getDiscountPercentage));
    }

    public Optional<Promotion> getBestPromotion(Long roomId) {
        List<Promotion> promotions = promotionRepository.findByRoomId(roomId);
        LocalDate today = LocalDate.now();
        return promotions.stream()
                .filter(p -> p.getStartDate().isBefore(today))
                .filter(p -> p.getEndDate().isAfter(today))
                .filter(p -> p.getStatus() == 1)
                .filter(p -> p.getQuantityUsed() < p.getQuantityRoom())
                .max(Comparator.comparing(Promotion::getDiscountPercentage));
    }

    public List<Promotion> getAllPromotions() {
        return promotionRepository.findByStatus(1);
    }

    public List<Promotion> getValidPromotions() {
        return promotionRepository.findValidPromotions(LocalDate.now());
    }

    public List<Promotion> getPromotionsByRoomId(Long roomId) {
        return promotionRepository.findByRoomId(roomId);
    }
    public PromotionResponse mapToResponse(Promotion promotion) {
    PromotionResponse response = new PromotionResponse();
    response.setId(promotion.getId());
    response.setRoomId(promotion.getRoomId());
    response.setDiscountPercentage(promotion.getDiscountPercentage());
    response.setStartDate(promotion.getStartDate());
    response.setEndDate(promotion.getEndDate());
    response.setQuantityRoom(promotion.getQuantityRoom());
    response.setQuantityUsed(promotion.getQuantityUsed());
    return response;
}

}
