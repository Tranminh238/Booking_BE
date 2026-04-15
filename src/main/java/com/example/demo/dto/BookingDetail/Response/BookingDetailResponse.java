package com.example.demo.dto.BookingDetail.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailResponse {
        private Long id;
        private Long roomId;
        private Integer numRoom;
        private Integer pricePerNight;
        private Integer numAdults;
        private Integer numChildren;
    }
