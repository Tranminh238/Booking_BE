package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Hotel;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByName(String name);
    @Query("SELECT h FROM Hotel h WHERE h.status = 1")
    List<Hotel> findHotelactive();
}
