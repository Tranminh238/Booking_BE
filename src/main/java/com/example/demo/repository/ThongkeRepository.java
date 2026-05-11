package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.dto.ThongkeDTO;
import com.example.demo.entity.Payment;

public interface ThongkeRepository extends JpaRepository<Payment, Long> {

     @Query("""
        SELECT DAY(p.createdAt) as day, SUM(p.amount) as revenue
        FROM Payment p
        LEFT JOIN Booking b ON p.bookingId = b.id
        LEFT JOIN Room r ON b.roomId = r.id
        LEFT JOIN Hotel h ON r.hotelId = h.id
        WHERE p.status = 2
          AND YEAR(p.createdAt) = :year
          AND MONTH(p.createdAt) = :month
        GROUP BY DAY(p.createdAt)
        ORDER BY DAY(p.createdAt)
    """)
    List<Object[]> findDailyRevenueByAdmin(
        @Param("year") int year,
        @Param("month") int month
    );

     @Query("""
        SELECT DAY(p.createdAt) as day, SUM(p.amount) as revenue
        FROM Payment p
        LEFT JOIN Booking b ON p.bookingId = b.id
        LEFT JOIN Room r ON b.roomId = r.id
        LEFT JOIN Hotel h ON r.hotelId = h.id
        WHERE h.userId = :userId
          AND p.status = 2
          AND YEAR(p.createdAt) = :year
          AND MONTH(p.createdAt) = :month
        GROUP BY DAY(p.createdAt)
        ORDER BY DAY(p.createdAt)
    """)
    List<Object[]> findDailyRevenueByPartner(
        @Param("userId") Long userId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("""
        SELECT DAY(p.createdAt) as day, SUM(p.amount) as revenue
        FROM Payment p
        LEFT JOIN Booking b ON p.bookingId = b.id
        LEFT JOIN Room r ON b.roomId = r.id
        LEFT JOIN Hotel h ON r.hotelId = h.id
        WHERE h.id = :hotelId
          AND p.status = 2
          AND YEAR(p.createdAt) = :year
          AND MONTH(p.createdAt) = :month
        GROUP BY DAY(p.createdAt)
        ORDER BY DAY(p.createdAt)
    """)
    List<Object[]> findDailyRevenueByHotel(
        @Param("hotelId") Long hotelId,
        @Param("year") int year,
        @Param("month") int month
    );
}
