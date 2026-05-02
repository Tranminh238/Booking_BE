package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.review.Response.ReviewResponse;
import com.example.demo.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHotelId(Long hotelId);

    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    @Query("""
        SELECT new com.example.demo.dto.review.Response.ReviewResponse(
            CONCAT(u.firstName, ' ', u.lastName),
            r.rating,
            r.comment,
            r.createdAt
        )
        FROM Review r, User u
        WHERE u.id = r.userId
        AND r.hotelId = :hotelId
    """)
    List<ReviewResponse> findReviewResponsesByHotelId(@Param("hotelId") Long hotelId);
}
