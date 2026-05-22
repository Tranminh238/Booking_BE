package com.example.demo.controller;

import com.example.demo.dto.Account.request.ChangePassword;
import com.example.demo.dto.Account.request.ForgotPassword;
import com.example.demo.dto.Account.request.RegistRequest;
import com.example.demo.dto.User.request.ClientEdditInfoRequest;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.demo.entity.Account;
import java.util.Map;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody @Valid RegistRequest registRequest){
        try {
            accountService.registerClient(registRequest);
            return ResponseEntity.ok(new BaseResponse(200, "Success", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @PostMapping("/register-partner")
    public ResponseEntity<BaseResponse> registerPartner(@RequestBody @Valid RegistRequest registRequest){
        try {
            accountService.registerPartner(registRequest);
            return ResponseEntity.ok(new BaseResponse(200, "Success", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @PostMapping("/edit-info")
    public ResponseEntity<BaseResponse> editInfo(@RequestBody ClientEdditInfoRequest request) {
        try {
            accountService.editInfo(request);
            return ResponseEntity.ok(new BaseResponse(200, "Success", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<BaseResponse> getClientInfo(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(new BaseResponse(200, "Success", accountService.getClientInfo(userId)));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse> changePassword(
            @AuthenticationPrincipal Account account,
            @RequestBody @Valid ChangePassword request) {
        try {
            if (account == null) {
                return ResponseEntity.ok(
                        new BaseResponse(401, "Bạn chưa đăng nhập hoặc token không hợp lệ", null)
                );
            }
            accountService.changePassword(account.getUsername(), request);
            return ResponseEntity.ok(
                    new BaseResponse(200, "Đổi mật khẩu thành công", null)
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    new BaseResponse(500, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<BaseResponse> requestOtp(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(400, "Email không được để trống", null));
            }
            accountService.requestOtp(email);
            return ResponseEntity.ok(new BaseResponse(200, "Mã OTP đã được gửi đến email của bạn", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<BaseResponse> resetPassword(@RequestBody @Valid ForgotPassword request) {
        try {
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.ok(new BaseResponse(400, "Mật khẩu mới không được để trống", null));
            }
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.ok(new BaseResponse(400, "Mật khẩu xác nhận không khớp", null));
            }
            accountService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok(new BaseResponse(200, "Đặt lại mật khẩu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<BaseResponse> softDeleteAccount(@PathVariable Long id){
        try {
            accountService.softDeleteAccount(id);
            return ResponseEntity.ok(new BaseResponse(200, "Success", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
    @PostMapping("/restore/{id}")
    public ResponseEntity<BaseResponse> softRestoreAccount(@PathVariable Long id){
        try {
            accountService.softRestoreAccount(id);
            return ResponseEntity.ok(new BaseResponse(200, "Success", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponse(500, e.getMessage(), null));
        }
    }
}
