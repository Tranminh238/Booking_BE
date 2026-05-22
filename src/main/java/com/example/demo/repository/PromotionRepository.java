package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("""
        SELECT p FROM Promotion p
        WHERE p.startDate <= :now
        AND p.endDate >= :now
        AND p.quantityUsed < p.quantityRoom
        AND p.status = 1
    """)
    List<Promotion> findValidPromotions(LocalDate now);

    @Query("""
        SELECT p FROM Promotion p
        WHERE p.roomId = :roomId
        AND p.status = 1
    """)
    List<Promotion> findByRoomId(Long roomId);

    List<Promotion> findByStatus(Integer status);

    @Query("""
        SELECT p FROM Promotion p
        JOIN Room r ON r.id = p.roomId
        WHERE r.id = :roomId
        AND p.startDate <= :date
        AND p.endDate >= :date
        AND p.status = 1
        """)
    List<Promotion> findActivePromotionsForRoomAndDate(
        @Param("roomId") Long roomId,
        @Param("date") LocalDate date
    );
}
