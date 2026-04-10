package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.demo.service.HotelService;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.dto.Hotel.request.HotelForm;
import com.example.demo.entity.Hotel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createHotel(@RequestBody HotelForm form) {
        try  {
            hotelService.createHotel(form); 
            return ResponseEntity.ok(new BaseResponse(200, "Thêm khách sạn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Thêm khách sạn thất bại", e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BaseResponse> updateHotel(@PathVariable Long id, @RequestBody HotelForm form) {
        try {
            hotelService.updateHotel(id, form);
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
    public ResponseEntity<BaseResponse> getAllHotels(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Hotel> result = hotelService.getAllHotels(page, size);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }

    @GetMapping("/getbyuserid/{userId}")
    public ResponseEntity<BaseResponse> getHotelByUserId(@PathVariable Long userId) {
        try {
            List<Hotel> result = hotelService.getHotelByUserId(userId);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }

    @GetMapping("/getbycity/{city}")
    public ResponseEntity<BaseResponse> getHotelByCity(@PathVariable String city, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Hotel> result = hotelService.getHotelByCity(city, page, size);
            return ResponseEntity.ok(new BaseResponse(200, "Lấy danh sách khách sạn thành công", result));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, "Lấy danh sách khách sạn thất bại", e.getMessage()));
        }
    }
}
