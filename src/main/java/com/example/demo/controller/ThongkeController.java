package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Account;
import com.example.demo.service.ThongKeService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/thongke")
public class ThongkeController {

    private final ThongKeService thongKeService;

    @GetMapping("/revenue/admin")
    public ResponseEntity<?> getMonthRevenueAdmin(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> result = thongKeService.getMonthSummaryAdmin(year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue/partner")
    public ResponseEntity<?> getMonthRevenuePartner(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long userId) {
        Map<String, Object> result = thongKeService.getMonthSummaryPartner(userId, year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue/hotel")
    public ResponseEntity<?> getMonthRevenueHotel(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long hotelId) {
        Map<String, Object> result = thongKeService.getMonthSummaryHotel(hotelId, year, month);
        return ResponseEntity.ok(result);
    }
}
