package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);

    @Query("SELECT MIN(r.pricePerNight) FROM Room r WHERE r.hotelId = :hotelId AND r.status = 1")
    Integer findMinPriceByHotelId(@Param("hotelId") Long hotelId);
}
