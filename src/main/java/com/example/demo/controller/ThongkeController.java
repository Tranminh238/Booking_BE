package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Account;
import com.example.demo.service.ThongKeService;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/thongke")
public class ThongkeController {
    private final ThongKeService thongKeService;

    @GetMapping("/revenue-hotel")
    public ResponseEntity<?> getMonthlyRevenueHotel(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate now = LocalDate.now();
        int resolvedYear = (year != null) ? year : now.getYear();
        int resolvedMonth = (month != null) ? month : now.getMonthValue();

        Map<String, Object> result = thongKeService.getMonthSummaryHotel(hotelId,resolvedYear, resolvedMonth);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-admin")
    public ResponseEntity<?> getMonthlyRevenueAdmin(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal Account account) {
        LocalDate now = LocalDate.now();
        int resolvedYear = (year != null) ? year : now.getYear();
        int resolvedMonth = (month != null) ? month : now.getMonthValue();

        Map<String, Object> result = thongKeService.getMonthSummaryAdmin(resolvedYear, resolvedMonth);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-partner")
    public ResponseEntity<?> getMonthlyRevenuePartner(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam Long userId) { 
        LocalDate now = LocalDate.now();
        int resolvedYear = (year != null) ? year : now.getYear();
        int resolvedMonth = (month != null) ? month : now.getMonthValue();

        Map<String, Object> result = thongKeService.getMonthSummaryPartner(userId, resolvedYear, resolvedMonth);
        return ResponseEntity.ok(result);
    }
}
