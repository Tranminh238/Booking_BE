package com.example.demo.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RoomAvailability;
import java.util.List;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
    boolean existsByRoomIdAndDate(Long roomId, LocalDate date);

    List<RoomAvailability> findByRoomIdAndDateBetween(
        Long roomId,
        LocalDate start,
        LocalDate end
    );

}
