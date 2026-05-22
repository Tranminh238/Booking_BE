package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.review.Request.ReviewRequest;
import com.example.demo.dto.review.Response.ReviewResponse;
import com.example.demo.entity.Review;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                reviewService.getReviewsByHotel(hotelId, pageable)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(
            @PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/create/{hotelId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long hotelId,
            @RequestBody ReviewRequest request,
            @RequestParam Long userId,
            @RequestParam Long bookingId) {
        try {
            Review review = reviewService.createReview(bookingId, userId, hotelId, request);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{bookingId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long bookingId,
            @RequestParam Long hotelId,
            @RequestBody ReviewRequest request) {
        try {
            reviewService.updateReview(bookingId, hotelId, request);
            return ResponseEntity.ok("Cập nhật đánh giá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long bookingId) {
        try {
            reviewService.deleteReview(bookingId);
            return ResponseEntity.ok("Xóa đánh giá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/total/{hotelId}")
    public ResponseEntity<Integer> getTotalReview(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getTotalReview(hotelId));
    }
}

