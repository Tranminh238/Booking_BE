package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.review.Response.ReviewResponse;
import com.example.demo.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHotelId(Long hotelId);

    boolean existsByBookingId(Long bookingId);

    Optional<Review> findByBookingId(Long bookingId);

    @Query("""
        SELECT new com.example.demo.dto.review.Response.ReviewResponse(
            r.id, r.bookingId, r.hotelId, h.name, ha.city, null,
            u.firstName, u.lastName,
            r.rating, r.comment, r.createdAt
        )
        FROM Review r
        LEFT JOIN Hotel h ON h.id = r.hotelId
        LEFT JOIN User u ON u.id = r.userId
        LEFT JOIN HotelAddress ha ON ha.hotelId = h.id
        WHERE r.hotelId = :hotelId
    """)
    List<ReviewResponse> findReviewResponsesByHotelId(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT COUNT(r.id)
        FROM Review r
        WHERE r.hotelId = :hotelId
    """)
    int totalReview(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT new com.example.demo.dto.review.Response.ReviewResponse(
            r.id, r.bookingId, r.hotelId, h.name, ha.city, null,
            u.firstName, u.lastName,
            r.rating, r.comment, r.createdAt
        )
        FROM Review r
        LEFT JOIN Hotel h ON h.id = r.hotelId
        LEFT JOIN User u ON u.id = r.userId
        LEFT JOIN HotelAddress ha ON ha.hotelId = h.id
        WHERE r.bookingId = :bookingId
    """)
    ReviewResponse findReviewByBookingId(@Param("bookingId") Long bookingId);

    @Query("""
        SELECT new com.example.demo.dto.review.Response.ReviewResponse(
            r.id, r.bookingId, r.hotelId, h.name, ha.city, null,
            u.firstName, u.lastName,
            r.rating, r.comment, r.createdAt
        )
        FROM Review r
        LEFT JOIN Hotel h ON h.id = r.hotelId
        LEFT JOIN User u ON u.id = r.userId
        LEFT JOIN HotelAddress ha ON ha.hotelId = h.id
        WHERE r.userId = :userId
        ORDER BY r.createdAt DESC
    """)
    List<ReviewResponse> findReviewResponsesByUserId(@Param("userId") Long userId);
}
