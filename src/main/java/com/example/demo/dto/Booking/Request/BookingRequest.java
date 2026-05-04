package com.example.demo.dto.Booking.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

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
    @NotBlank(message = "Số phòng không được để trống")
    private Integer numRoom;
    @NotBlank(message = "Số người lớn không được để trống")
    private Integer numAdults;
    @NotBlank(message = "Số trẻ em không được để trống")
    private Integer numChildren;
    private Integer pricePerNight;
    private String paymentMethod;
    @NotBlank(message = "Tên liên hệ không được để trống")
    private String contactName;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String contactPhone;
    @NotBlank(message = "Email không được để trống")
    private String contactEmail;
}
