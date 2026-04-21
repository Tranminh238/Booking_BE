package com.example.demo.controller;

import com.example.demo.dto.Account.request.RegistRequest;
import com.example.demo.dto.User.request.ClientEdditInfoRequest;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
