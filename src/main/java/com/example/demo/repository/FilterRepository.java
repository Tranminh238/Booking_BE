package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.dto.Hotel.request.HotelFilter;

public interface FilterRepository {
    Page<HotelResponse> filterHotel(Pageable pageable, HotelFilter hotelFilter);
}
