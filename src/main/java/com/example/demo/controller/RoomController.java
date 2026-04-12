package com.example.demo.controller;

import com.example.demo.dto.Room.request.RoomForm;
import com.example.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.dto.Room.response.RoomResponse;
import org.springframework.data.domain.Page;
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

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateRoom(
            @PathVariable Long id,
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
            return ResponseEntity.ok(roomService.updateRoom(id, form, images));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(new BaseResponse(200, "Room deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<BaseResponse> getRoomDetail(@PathVariable Long id) {
        try {
            RoomResponse response = roomService.getRoomDetail(id);
            return ResponseEntity.ok(new BaseResponse(200, "Success", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<BaseResponse> getRoomsByHotelId(@PathVariable Long hotelId) {
        try {
            List<RoomResponse> rooms = roomService.getRoomsByHotelId(hotelId);
            return ResponseEntity.ok(new BaseResponse(200, "Success", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<BaseResponse> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<RoomResponse> rooms = roomService.getAllRooms(page, size);
            return ResponseEntity.ok(new BaseResponse(200, "Success", rooms));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }
}
