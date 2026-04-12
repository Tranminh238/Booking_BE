package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.sql.Time;
import java.util.List;
import com.example.demo.service.HotelService;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.dto.Hotel.request.HotelForm;
import com.example.demo.dto.Hotel.response.HotelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createHotel(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Time checkin_time_start,
            @RequestParam(required = false) Time checkin_time_end,
            @RequestParam(required = false) Time checkout_time_start,
            @RequestParam(required = false) Time checkout_time_end,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<Long> amenityIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "policyFiles", required = false) List<MultipartFile> policyFiles
    ) {
        try {
            HotelForm form = HotelForm.builder()
                    .userId(userId)
                    .name(name)
                    .star(star)
                    .description(description)
                    .checkin_time_start(checkin_time_start)
                    .checkin_time_end(checkin_time_end)
                    .checkout_time_start(checkout_time_start)
                    .checkout_time_end(checkout_time_end)
                    .district(district)
                    .city(city)
                    .country(country)
                    .amenityIds(amenityIds)
                    .build();
            hotelService.createHotel(form, images, policyFiles);
            return ResponseEntity.ok(new BaseResponse(200, "Thêm khách sạn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Thêm khách sạn thất bại", e.getMessage()));
        }
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateHotel(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer star,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Time checkin_time_start,
            @RequestParam(required = false) Time checkin_time_end,
            @RequestParam(required = false) Time checkout_time_start,
            @RequestParam(required = false) Time checkout_time_end,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) List<Long> amenityIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "policyFiles", required = false) List<MultipartFile> policyFiles
    ) {
        try {
            HotelForm form = HotelForm.builder()
                    .name(name)
                    .star(star)
                    .status(status)
                    .description(description)
                    .checkin_time_start(checkin_time_start)
                    .checkin_time_end(checkin_time_end)
                    .checkout_time_start(checkout_time_start)
                    .checkout_time_end(checkout_time_end)
                    .district(district)
                    .city(city)
                    .country(country)
                    .amenityIds(amenityIds)
                    .build();
            hotelService.updateHotel(id, form, images, policyFiles);
            return ResponseEntity.ok(new BaseResponse(200, "Cập nhật khách sạn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Cập nhật khách sạn thất bại", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteHotel(@PathVariable Long id) {
        try {
            hotelService.deleteHotel(id);
            return ResponseEntity.ok(new BaseResponse(200, "Xóa khách sạn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Xóa khách sạn thất bại", e.getMessage()));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<BaseResponse> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<HotelResponse> result = hotelService.getAllHotels(page, size);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }

    @GetMapping("/getbyuserid/{userId}")
    public ResponseEntity<BaseResponse> getHotelByUserId(@PathVariable Long userId) {
        try {
            List<HotelResponse> result = hotelService.getHotelByUserId(userId);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }

    @GetMapping("/getbycity/{city}")
    public ResponseEntity<BaseResponse> getHotelByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<HotelResponse> result = hotelService.getHotelByCity(city, page, size);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }
}
