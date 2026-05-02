package com.example.demo.dto.Booking.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.dto.BookingDetail.Response.BookingDetailResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer totalPrice;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private List<BookingDetailResponse> details;
}
