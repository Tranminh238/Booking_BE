package com.example.demo.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ThongkeDTO;
import com.example.demo.entity.Hotel;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ThongkeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThongKeService {
    private final ThongkeRepository thongkeRepository;
    private final HotelRepository hotelRepository;
    
    // Thống kê doanh thu theo Admin
    public Map<String, Object> getMonthSummaryAdmin(int year, int month) {
        List<Object[]> raw = thongkeRepository
            .findDailyRevenueByAdmin(year, month);

        List<ThongkeDTO> daily = raw.stream()
            .map(row -> new ThongkeDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).longValue()
            ))
            .collect(Collectors.toList());

        long totalRevenue = daily.stream().mapToLong(ThongkeDTO::getRevenue).sum();
        long maxDay       = daily.stream().mapToLong(ThongkeDTO::getRevenue).max().orElse(0);
        long avgDay       = daily.isEmpty() ? 0 :
            Math.round(daily.stream().mapToLong(ThongkeDTO::getRevenue).average().orElse(0));

        Long totalBookingVal = thongkeRepository.getTotalBookingAdmin();
        long totalBooking = totalBookingVal != null ? totalBookingVal : 0L;

        Long totalBookingByMonthVal = thongkeRepository.getTotalBookingByMonthAdmin(year, month);
        long totalBookingByMonth = totalBookingByMonthVal != null ? totalBookingByMonthVal : 0L;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalRevenue", totalRevenue);
        summary.put("maxDayRevenue", maxDay);
        summary.put("avgDayRevenue", avgDay);
        summary.put("totalBooking", totalBooking);
        summary.put("totalBookingByMonth", totalBookingByMonth);
        summary.put("daily", daily);
        return summary;
    }
    // Thống kê doanh thu theo partner
    public Map<String, Object> getMonthSummaryPartner(Long userId, int year, int month) {
        List<Object[]> raw = thongkeRepository
            .findDailyRevenueByPartner(userId, year, month);

        List<ThongkeDTO> daily = raw.stream()
            .map(row -> new ThongkeDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).longValue()
            ))
            .collect(Collectors.toList());

        long totalRevenue = daily.stream().mapToLong(ThongkeDTO::getRevenue).sum();
        long maxDay       = daily.stream().mapToLong(ThongkeDTO::getRevenue).max().orElse(0);
        long avgDay       = daily.isEmpty() ? 0 :
            Math.round(daily.stream().mapToLong(ThongkeDTO::getRevenue).average().orElse(0));

        Long totalBookingVal = thongkeRepository.getTotalBookingByPartner(userId);
        long totalBooking = totalBookingVal != null ? totalBookingVal : 0L;

        Long totalBookingByMonthVal = thongkeRepository.getTotalBookingByMonth(userId, year, month);
        long totalBookingByMonth = totalBookingByMonthVal != null ? totalBookingByMonthVal : 0L;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalRevenue", totalRevenue);
        summary.put("maxDayRevenue", maxDay);
        summary.put("avgDayRevenue", avgDay);
        summary.put("totalBooking", totalBooking);
        summary.put("totalBookingByMonth", totalBookingByMonth);
        summary.put("daily", daily);
        return summary;
    }
    public Map<String, Object> getMonthSummaryHotel(Long hotelId, int year, int month) {
        List<Object[]> raw = thongkeRepository
            .findDailyRevenueByHotel(hotelId, year, month);

        List<ThongkeDTO> daily = raw.stream()
            .map(row -> new ThongkeDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).longValue()
            ))
            .collect(Collectors.toList());

        long totalRevenue = daily.stream().mapToLong(ThongkeDTO::getRevenue).sum();
        long maxDay       = daily.stream().mapToLong(ThongkeDTO::getRevenue).max().orElse(0);
        long avgDay       = daily.isEmpty() ? 0 :
            Math.round(daily.stream().mapToLong(ThongkeDTO::getRevenue).average().orElse(0));

        Long totalBookingVal = thongkeRepository.getTotalBookingByHotel(hotelId);
        long totalBooking = totalBookingVal != null ? totalBookingVal : 0L;

        Long totalBookingByMonthVal = thongkeRepository.getTotalBookingByMonthByHotel(hotelId, year, month);
        long totalBookingByMonth = totalBookingByMonthVal != null ? totalBookingByMonthVal : 0L;

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("year", year);
        summary.put("month", month);
        summary.put("totalRevenue", totalRevenue);
        summary.put("maxDayRevenue", maxDay);
        summary.put("avgDayRevenue", avgDay);
        summary.put("totalBooking", totalBooking);
        summary.put("totalBookingByMonth", totalBookingByMonth);
        summary.put("daily", daily);
        return summary;
    }
    
}
