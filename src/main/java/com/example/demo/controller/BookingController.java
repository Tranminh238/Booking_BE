package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.BookingService;
import com.example.demo.service.VnPayService;
import com.example.demo.dto.Booking.Request.BookingRequest;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final VnPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest req) {
        try {
            return ResponseEntity.ok(bookingService.createBooking(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/complete/{bookingId}")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(bookingService.completeBooking(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<?> getBookingsByHotelId(@PathVariable Long hotelId) {
        try {
            return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/partner/{userId}")
    public ResponseEntity<?> getBookingByPartnerId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingByPartnerId(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        try {
            return ResponseEntity.ok(bookingService.getAllBookings());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/payment/{bookingId}")
    public ResponseEntity<?> createPaymentUrl(
            @PathVariable Long bookingId,
            HttpServletRequest request) {
        try {
            String paymentUrl = vnPayService.createPaymentUrl(bookingId, request);
            return ResponseEntity.ok(paymentUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/payment/callback")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Xác thực chữ ký VNPay và cập nhật trạng thái Booking + Payment trong DB
        vnPayService.resultPayment(request);

        // Sau khi cập nhật DB, redirect browser về trang frontend kèm toàn bộ query params của VNPay
        // Frontend sẽ parse các params này để hiển thị màn hình kết quả
        String queryString = request.getQueryString(); // vd: vnp_ResponseCode=00&vnp_Amount=...
        String frontendUrl = "http://localhost:3000/payment";
        if (queryString != null && !queryString.isEmpty()) {
            frontendUrl += "?" + queryString;
        }
        response.sendRedirect(frontendUrl);
    }
}
