package com.example.demo.controller;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;
    @PostMapping("/toggle")
    public ResponseEntity<BaseResponse> toggleWishlist(@RequestParam Long userId, @RequestParam Long hotelId) {
        try {
            boolean isAdded = wishlistService.toggleWishlist(userId, hotelId);
            return ResponseEntity.ok(new BaseResponse(200, isAdded ? "Added to wishlist" : "Removed from wishlist", isAdded));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse> getWishlist(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(new BaseResponse(200, "Success", wishlistService.getWishlistHotels(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @GetMapping("/user/{userId}/ids")
    public ResponseEntity<BaseResponse> getWishlistIds(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(new BaseResponse(200, "Success", wishlistService.getWishlistHotelIds(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new BaseResponse(500, e.getMessage(), null));
        }
    }
}