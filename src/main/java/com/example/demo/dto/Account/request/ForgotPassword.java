package com.example.demo.dto.Account.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPassword {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}
