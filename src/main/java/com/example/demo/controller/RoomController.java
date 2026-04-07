package com.example.demo.controller;

import com.example.demo.dto.Room.request.RoomForm;
import com.example.demo.dto.Room.response.RoomResponse;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.base.BaseResponse;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createRoom(@Valid @RequestBody RoomForm form) {
        try{
            return ResponseEntity.ok(roomService.createRoom(form));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new BaseResponse(500, "Error", e.getMessage()));
        }
    }
}
