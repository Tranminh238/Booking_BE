package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RoomAmenities;

@Repository
public interface RoomAmenitiesRepository extends JpaRepository<RoomAmenities, Long> {
    
}
