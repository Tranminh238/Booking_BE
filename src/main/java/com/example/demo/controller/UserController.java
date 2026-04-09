package com.example.demo.controller;

import com.example.demo.dto.Account.request.AuthRequest;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }

    @PostMapping("/login-partner")
    public ResponseEntity<BaseResponse> loginPartner(@RequestBody @Valid AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.loginPartner(request));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
}
