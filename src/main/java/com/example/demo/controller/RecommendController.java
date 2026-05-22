package com.example.demo.controller;

import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /**
     * Gợi ý khách sạn cho người dùng đã đăng nhập
     * dựa trên lịch sử đặt phòng, tìm kiếm và wishlist
     */
    @GetMapping("/hotels")
    public ResponseEntity<BaseResponse> getRecommendations(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "6") int limit) {
        try {
            List<HotelResponse> hotels = recommendService.getRecommendations(userId, limit);

            return ResponseEntity.ok(new BaseResponse(200, "Success", hotels));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Error: " + e.getMessage(), null));
        }
    }

    /**
     * Lấy khách sạn phổ biến (không cần đăng nhập)
     */
    // @GetMapping("/popular")
    // public ResponseEntity<BaseResponse> getPopular(
    //         @RequestParam(defaultValue = "6") int limit) {
    //     try {
    //         List<HotelResponse> hotels = recommendService.getPopularHotels(limit);
    //         return ResponseEntity.ok(new BaseResponse(200, "Success", hotels));
    //     } catch (Exception e) {
    //         return ResponseEntity.ok(new BaseResponse(500, "Error: " + e.getMessage(), null));
    //     }
    // }
}
