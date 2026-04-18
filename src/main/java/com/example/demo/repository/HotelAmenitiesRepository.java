package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.HotelAmenities;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface HotelAmenitiesRepository extends JpaRepository<HotelAmenities, Long> {
    
    @Query("SELECT a.name FROM Amenity a JOIN HotelAmenities ha ON a.id = ha.amenityId WHERE ha.hotelId = :hotelId")
    List<String> findAmenityNamesByHotelId(@Param("hotelId") Long hotelId);
}
