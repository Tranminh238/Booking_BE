package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.HotelAddress;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelAddressRepository extends JpaRepository<HotelAddress, Long> {
    
    List<HotelAddress> findAll();
    boolean existsByDistrictAndCityAndCountry(String district, String city, String country);
    Optional<HotelAddress> findByHotelId(Long hotelId);
}
