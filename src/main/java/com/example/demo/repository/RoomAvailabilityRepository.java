package com.example.demo.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.RoomAvailability;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
    boolean existsByRoomIdAndDate(Long roomId, LocalDate date);

    void deleteByRoomId(Long roomId);

    List<RoomAvailability> findByRoomIdAndDateBetween(
        Long roomId,
        LocalDate start,
        LocalDate end
    );

    Optional<RoomAvailability> findByRoomIdAndDate(Long roomId, LocalDate date);

    /**
     * Kiểm tra xem phòng có còn đủ số lượng trong khoảng ngày không.
     * Trả về số bản ghi có quantityAvailable < numRoom (tức là không đủ phòng).
     */
    @Query("SELECT COUNT(ra) FROM RoomAvailability ra " +
           "WHERE ra.roomId = :roomId " +
           "AND ra.date >= :checkIn AND ra.date < :checkOut " +
           "AND ra.quantityAvailable < :numRoom")
    long countUnavailableDays(
        @Param("roomId") Long roomId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("numRoom") int numRoom
    );
}
