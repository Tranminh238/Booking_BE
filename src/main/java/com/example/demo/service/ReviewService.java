package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.review.Request.ReviewRequest;
import com.example.demo.dto.review.Response.ReviewResponse;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.HotelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UsersRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    public void createReview(Long userId, ReviewRequest request) {

        // ❌ check đã review chưa
        if (reviewRepository.existsByUserIdAndHotelId(userId, request.getHotelId())) {
            throw new RuntimeException("Bạn đã đánh giá khách sạn này rồi!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Review review = new Review();
        review.setUserId(user.getId());
        review.setHotelId(hotel.getId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);

        updateHotelRating(hotel.getId());
    }

    public List<ReviewResponse> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findReviewResponsesByHotelId(hotelId);
    }

    private void updateHotelRating(Long hotelId) {
        hotelService.updateAverageRating(hotelId);
    }
}
