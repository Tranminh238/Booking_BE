package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.BookingService;
import com.example.demo.dto.Booking.Request.BookingRequest;
import com.example.demo.dto.Booking.Response.BookingResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    // private final VnPayService vnPayService;

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

    // @PostMapping("/payment/{bookingId}")
    // public ResponseEntity<?> createPaymentUrl(@PathVariable Long bookingId) {
    //     try {
    //         return ResponseEntity.ok(VnPayService.createPaymentUrl(bookingId));
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }
}
