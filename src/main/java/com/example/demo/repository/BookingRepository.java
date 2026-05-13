package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Booking;
import com.example.demo.dto.Booking.Response.BookingDetailDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
       @Query("SELECT new com.example.demo.dto.Booking.Response.BookingDetailDTO(" +
                     "b.id, u.id, r.id, h.id, p.id, rt.id, " +
                     "h.name, ha.city, ha.district, rt.name, " +
                     "b.contactName, b.contactPhone, b.contactEmail, " +
                     "p.status, b.status, " +
                     "b.checkInDate, b.checkOutDate, " +
                     "bd.numRoom, bd.numAdults, bd.numChildren, b.totalPrice" +
                     ") " +
                     "FROM Booking b " +
                     "LEFT JOIN BookingDetail bd ON b.id = bd.bookingId " +
                     "LEFT JOIN User u ON b.userId = u.id " +
                     "LEFT JOIN Room r ON b.roomId = r.id " +
                     "LEFT JOIN Hotel h ON r.hotelId = h.id " +
                     "LEFT JOIN HotelAddress ha ON h.id = ha.hotelId " +
                     "LEFT JOIN Payment p ON b.id = p.bookingId " +
                     "LEFT JOIN RoomType rt ON r.roomTypeId = rt.id " +
                     "WHERE b.userId = :userId " +
                     "ORDER BY b.id DESC")
       List<BookingDetailDTO> findByUserId(@Param("userId") Long userId);

       List<Booking> findByRoomId(Long roomId);

       List<Booking> findByUserIdAndStatus(Long userId, Integer status);

    @Query("SELECT new com.example.demo.dto.Booking.Response.BookingDetailDTO(" +
           "b.id, u.id, r.id, h.id, p.id, rt.id, " +
           "h.name, ha.city, ha.district, rt.name, " +
           "b.contactName, b.contactPhone, b.contactEmail, " +
           "p.status, b.status, " +
           "b.checkInDate, b.checkOutDate, " +
           "bd.numRoom, bd.numAdults, bd.numChildren, b.totalPrice" +
           ") " +
           "FROM Booking b " +
           "LEFT JOIN BookingDetail bd ON b.id = bd.bookingId " +
           "JOIN User u ON b.userId = u.id " +
           "LEFT JOIN Room r ON b.roomId = r.id " +
           "JOIN Hotel h ON r.hotelId = h.id " +
           "LEFT JOIN HotelAddress ha ON h.id = ha.hotelId " +
           "LEFT JOIN Payment p ON b.id = p.bookingId " +
           "LEFT JOIN RoomType rt ON r.roomTypeId = rt.id " +
           "ORDER BY b.id DESC")
    List<BookingDetailDTO> getAllBookings();

       @Query("SELECT new com.example.demo.dto.Booking.Response.BookingDetailDTO(" +
                     "b.id, u.id, r.id, h.id, p.id, rt.id, " +
                     "h.name, ha.city, ha.district, rt.name, " +
                     "b.contactName, b.contactPhone, b.contactEmail, " +
                     "p.status, b.status, " +
                     "b.checkInDate, b.checkOutDate, " +
                     "bd.numRoom, bd.numAdults, bd.numChildren, b.totalPrice" +
                     ") " +
                     "FROM Booking b " +
                     "LEFT JOIN BookingDetail bd ON b.id = bd.bookingId " +
                     "JOIN User u ON b.userId = u.id " +
                     "LEFT JOIN Room r ON b.roomId = r.id " +
                     "JOIN Hotel h ON r.hotelId = h.id " +
                     "LEFT JOIN HotelAddress ha ON h.id = ha.hotelId " +
                     "LEFT JOIN Payment p ON b.id = p.bookingId " +
                     "LEFT JOIN RoomType rt ON r.roomTypeId = rt.id " +
                     "WHERE h.id = :hotelId " +
                     "ORDER BY b.id DESC")
       List<BookingDetailDTO> getAllBookingsByHotelId(@Param("hotelId") Long hotelId);

       @Query("SELECT new com.example.demo.dto.Booking.Response.BookingDetailDTO(" +
                     "b.id, u.id, r.id, h.id, p.id, rt.id, " +
                     "h.name, ha.city, ha.district, rt.name, " +
                     "b.contactName, b.contactPhone, b.contactEmail, " +
                     "p.status, b.status, " +
                     "b.checkInDate, b.checkOutDate, " +
                     "bd.numRoom, bd.numAdults, bd.numChildren, b.totalPrice" +
                     ") " +
                     "FROM Booking b " +
                     "LEFT JOIN BookingDetail bd ON b.id = bd.bookingId " +
                     "JOIN User u ON b.userId = u.id " +
                     "LEFT JOIN Room r ON b.roomId = r.id " +
                     "JOIN Hotel h ON r.hotelId = h.id " +
                     "LEFT JOIN HotelAddress ha ON h.id = ha.hotelId " +
                     "LEFT JOIN Payment p ON b.id = p.bookingId " +
                     "LEFT JOIN RoomType rt ON r.roomTypeId = rt.id " +
                     "WHERE h.userId = :userId " +
                     "ORDER BY b.id DESC")
       List<BookingDetailDTO> getBookingByPartnerId(@Param("userId") Long userId);
}
