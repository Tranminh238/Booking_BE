package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.HotelPolicy;
import java.util.Optional;

public interface HotelPolicyRepository extends JpaRepository<HotelPolicy, Long> {
    Optional<HotelPolicy> findByHotelId(Long hotelId);
}
