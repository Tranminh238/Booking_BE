package com.example.demo.dto.Booking.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BookingDetailDTO {
    private Long id;
    private Long userId;
    private Long roomId;
    private Long hotelId;
    private Long paymentId;
    private Long roomTypeId;
    private String hotelName;
    private String city;
    private String district;
    private String roomTypeName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer paymentStatus;
    private Integer bookingStatus;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numRoom;
    private Integer numAdults;
    private Integer numChildren;
    private Integer totalPrice;
    private List<String> imageUrl;

    // Constructor cho JPQL (không có imageUrl)
    public BookingDetailDTO(Long id, Long userId, Long roomId, Long hotelId, Long paymentId,
                             Long roomTypeId, String hotelName, String city, String district, String roomTypeName,
                             String contactName, String contactPhone, String contactEmail,
                             Integer paymentStatus, Integer bookingStatus,
                             LocalDate checkInDate, LocalDate checkOutDate,
                             Integer numRoom, Integer numAdults, Integer numChildren,
                             Integer totalPrice) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.hotelId = hotelId;
        this.paymentId = paymentId;
        this.roomTypeId = roomTypeId;
        this.hotelName = hotelName;
        this.city = city;
        this.district = district;
        this.roomTypeName = roomTypeName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numRoom = numRoom;
        this.numAdults = numAdults;
        this.numChildren = numChildren;
        this.totalPrice = totalPrice;
    }
}