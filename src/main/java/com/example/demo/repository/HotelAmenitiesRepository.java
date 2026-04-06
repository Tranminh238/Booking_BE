package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.HotelAmenities;

@Repository
public interface HotelAmenitiesRepository extends JpaRepository<HotelAmenities, Long> {
    
}
