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

    @Query("SELECT u.firstName, u.lastName, u.email, u.phoneNumber, h.name, rt.name, b.checkInDate, b.checkOutDate, b.totalPrice, p.status FROM Booking b JOIN   User u ON b.userId = u.id JOIN Room r ON b.roomId = r.id JOIN Hotel h ON r.hotelId = h.id JOIN Payment p ON b.id = p.bookingId Join RoomType rt ON r.roomTypeId = rt.id")
    List<Object[]> getAllBookings();

    @Query("SELECT u.firstName, u.lastName, u.email, u.phoneNumber, h.name, rt.name, b.checkInDate, b.checkOutDate, b.totalPrice, p.status FROM Booking b JOIN   User u ON b.userId = u.id JOIN Room r ON b.roomId = r.id JOIN Hotel h ON r.hotelId = h.id JOIN Payment p ON b.id = p.bookingId Join RoomType rt ON r.roomTypeId = rt.id WHERE h.id = :hotelId")
    List<Object[]> getAllBookingsByHotelId(Long hotelId);

    @Query("""
        SELECT u.firstName, u.lastName, u.email, u.phoneNumber,
            h.name, rt.name,
            b.checkInDate, b.checkOutDate, b.totalPrice,
            p.status
        FROM Booking b
        JOIN User u ON b.userId = u.id
        JOIN Room r ON b.roomId = r.id
        JOIN Hotel h ON r.hotelId = h.id
        JOIN Payment p ON b.id = p.bookingId
        JOIN RoomType rt ON r.roomTypeId = rt.id
        WHERE h.userId = :userId
        """)
List<Object[]> getBookingByUserId(@Param("userId") Long userId);
}
