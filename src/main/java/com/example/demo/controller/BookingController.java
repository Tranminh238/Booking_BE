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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
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
    public ResponseEntity<?> paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int result = vnPayService.resultPayment(request);
            if (result == 1) {
                return ResponseEntity.ok("Thanh toán thành công");
            }
            return ResponseEntity.badRequest().body("Thanh toán thất bại");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
