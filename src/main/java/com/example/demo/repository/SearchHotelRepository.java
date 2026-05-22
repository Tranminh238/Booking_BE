package com.example.demo.repository;

import com.example.demo.entity.SearchHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHotelRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT s FROM SearchHistory s WHERE s.userId = :userId ORDER BY s.id DESC")
    List<SearchHistory> findRecentByUserId(@Param("userId") Long userId);
}
