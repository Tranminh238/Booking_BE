package com.example.demo.dto.Booking.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
    // Thông tin chi tiết BookingDetail
    private Integer numRoom;
    private Integer numAdults;
    private Integer numChildren;
    private Integer pricePerNight;
    // Trạng thái thanh toán
    private String paymentMethod; // "VNPAY", "CASH", ...
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
