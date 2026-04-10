package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.entity.Hotel;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByName(String name);

    @Query("SELECT h FROM Hotel h WHERE h.status = 1")
    Page<Hotel> findHotelactive(Pageable pageable);

    @Query("SELECT h FROM Hotel h WHERE h.userId = :userId")
    List<Hotel> findHotelByUserId(Long userId);

    @Query("SELECT h FROM Hotel h WHERE h.userId = :userId")
    List<Hotel> findHotelByUserId(Long userId, Pageable pageable);

    @Query("SELECT h FROM Hotel h JOIN HotelAddress ha ON h.id = ha.hotelId WHERE ha.city = :city AND h.status = 1")
    Page<Hotel> findHotelByCity(@Param("city") String city, Pageable pageable);

}
