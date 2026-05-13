package com.example.demo.dto.Booking.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {
    @NotNull(message = "Không được để trống")
    private Long userId;
    @NotNull(message = "Không được để trống")
    private Long roomId;
    @NotNull(message = "Không được để trống")
    private LocalDate checkInDate;
    @NotNull(message = "Không được để trống")
    private LocalDate checkOutDate;
    @NotNull(message = "Không được để trống")
    private Integer totalPrice;
    @NotNull(message = "Số phòng không được để trống")
    private Integer numRoom;
    @NotNull(message = "Số người lớn không được để trống")
    private Integer numAdults;
    @NotNull(message = "Số trẻ em không được để trống")
    private Integer numChildren;
    @NotNull(message = "Giá phòng không được để trống")
    private Integer pricePerNight;
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
    private String message;
    @NotBlank(message = "Tên liên hệ không được để trống")
    private String contactName;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String contactPhone;
    @NotBlank(message = "Email không được để trống")
    private String contactEmail;
}
