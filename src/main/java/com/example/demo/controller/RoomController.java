package com.example.demo.controller;

import com.example.demo.dto.Room.request.RoomForm;
import com.example.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.base.BaseResponse;
import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createRoom(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) Integer pricePerNight,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Integer area,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) List<Long> amenityIds,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            RoomForm form = RoomForm.builder()
                    .hotelId(hotelId)
                    .roomTypeId(roomTypeId)
                    .pricePerNight(pricePerNight)
                    .capacity(capacity)
                    .quantity(quantity)
                    .area(area)
                    .status(status)
                    .description(description)
                    .amenityIds(amenityIds)
                    .build();
            return ResponseEntity.ok(roomService.createRoom(form, images));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }
}
