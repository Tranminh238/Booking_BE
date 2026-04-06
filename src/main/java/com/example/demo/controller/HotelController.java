package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.service.HotelService;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.dto.Hotel.request.HotelForm;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping("/create")
    public BaseResponse createHotel(@RequestBody HotelForm form) {
        try  {
            return hotelService.createHotel(form);
        } catch (Exception e) {
            return new BaseResponse(500, "Error", e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public BaseResponse updateHotel(@PathVariable Long id, @RequestBody HotelForm form) {
        try {
            return hotelService.updateHotel(id, form);
        } catch (Exception e) {
            return new BaseResponse(500, "Error", e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse deleteHotel(@PathVariable Long id) {
        try {
            hotelService.deleteHotel(id);
            return new BaseResponse(200, "Success", null);
        } catch (Exception e) {
            return new BaseResponse(500, "Error", e.getMessage());
        }
    }

    @GetMapping("/getall")
    public BaseResponse getAllHotels() {
        try {
            return hotelService.getAllHotels();
        } catch (Exception e) {
            return new BaseResponse(500, "Error", e.getMessage());
        }
    }

    @GetMapping("/getbyid/{id}")
    public BaseResponse getHotelById(@PathVariable Long id) {
        try {
            return hotelService.getHotelById(id);
        } catch (Exception e) {
            return new BaseResponse(500, "Error", e.getMessage());
        }
    }
}
