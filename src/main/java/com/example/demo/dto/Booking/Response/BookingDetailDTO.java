package com.example.demo.dto.Booking.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor  // ← bắt buộc phải có để JPQL gọi constructor
public class BookingDetailDTO {
    private Long id;           // b.id
    private Long userId;       // u.id
    private Long roomId;       // r.id
    private Long hotelId;      // h.id
    private Long paymentId;    // p.id
    private Long roomTypeId;   // rt.id
    private String hotelName;  // h.name
    private String city;       // ha.city
    private String roomTypeName; // rt.name
    private String contactName;  // b.contactName
    private String contactPhone; // b.contactPhone
    private String contactEmail; // b.contactEmail
    private Integer paymentStatus;  // p.status
    private Integer bookingStatus;  // b.status
    private LocalDate checkInDate;  // b.checkInDate
    private LocalDate checkOutDate; // b.checkOutDate
    private Integer numRoom;     // bd.numRoom
    private Integer numAdults;   // bd.numAdults
    private Integer numChildren; // bd.numChildren
    private Integer totalPrice; // b.totalPrice
}