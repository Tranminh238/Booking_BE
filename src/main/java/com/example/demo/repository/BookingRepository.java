package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByUserIdAndStatus(Long userId, Integer status);

    @Query("SELECT u.firstName, u.lastName, u.email, u.phoneNumber, h.name, rt.name, b.checkInDate, b.checkOutDate, b.totalPrice, p.status " +
           "FROM Booking b, User u, Room r, Hotel h, Payment p, RoomType rt " +
           "WHERE b.userId = u.id AND b.roomId = r.id AND r.hotelId = h.id AND b.id = p.bookingId AND r.roomTypeId = rt.id")
    List<Object[]> getAllBookings();

    @Query("SELECT u.firstName, u.lastName, u.email, u.phoneNumber, h.name, rt.name, b.checkInDate, b.checkOutDate, b.totalPrice, p.status " +
           "FROM Booking b, User u, Room r, Hotel h, Payment p, RoomType rt " +
           "WHERE b.userId = u.id AND b.roomId = r.id AND r.hotelId = h.id AND b.id = p.bookingId AND r.roomTypeId = rt.id AND h.id = :hotelId")
    List<Object[]> getAllBookingsByHotelId(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT u.firstName, u.lastName, u.email, u.phoneNumber,
            h.name, rt.name,
            b.checkInDate, b.checkOutDate, b.totalPrice,
            p.status
        FROM Booking b, User u, Room r, Hotel h, Payment p, RoomType rt
        WHERE b.userId = u.id 
          AND b.roomId = r.id 
          AND r.hotelId = h.id 
          AND b.id = p.bookingId 
          AND r.roomTypeId = rt.id 
          AND h.userId = :userId
        """)
    List<Object[]> getBookingByPartnerId(@Param("userId") Long userId);
}
