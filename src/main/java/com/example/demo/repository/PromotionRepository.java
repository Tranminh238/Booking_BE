package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
