package com.example.demo.controller;

import com.example.demo.dto.Account.request.AuthRequest;
import com.example.demo.dto.User.response.UserResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid AuthRequest request) {
        try {
            return ResponseEntity.ok(userService.login(request));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }

    @PostMapping("/login-partner")
    public ResponseEntity<BaseResponse> loginPartner(@RequestBody @Valid AuthRequest request) {
        try {
            return ResponseEntity.ok(userService.loginPartner(request));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }

    @GetMapping("/all-user")
    public Page<UserResponse> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable) {
        return userService.getAllUser(pageable);
    }

    @GetMapping("/all-user-active")
    public Page<UserResponse> getAllUserActive(
            @PageableDefault(size = 10) Pageable pageable) {
        return userService.getAllUserActive(pageable);
    }

    @GetMapping("/all-user-inactive")
    public Page<UserResponse> getAllUserInactive(
            @PageableDefault(size = 10) Pageable pageable) {
        return userService.getAllUserInactive(pageable);
    }
}
