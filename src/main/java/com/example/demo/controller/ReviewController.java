package com.example.demo.controller;

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
    public ResponseEntity<List<ReviewResponse>> getReviewsByHotel(
            @PathVariable Long hotelId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByHotel(hotelId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/create/{hotelId}")
    public ResponseEntity<?> createReview(
            @PathVariable Long hotelId,
            @RequestBody ReviewRequest request,
            @RequestParam Long userId) {
        try{
            Review review = reviewService.createReview(userId,hotelId, request);
            return ResponseEntity.ok(review);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/total/{hotelId}")
    public ResponseEntity<Integer> getTotalReview(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getTotalReview(hotelId));
    }
}
