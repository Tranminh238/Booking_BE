package com.example.demo.dto.review.Response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long bookingId;
    private Long hotelId;
    private String name; //hotelName
    private String city;
    private String imageUrl;
    private String firstName;
    private String lastName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
