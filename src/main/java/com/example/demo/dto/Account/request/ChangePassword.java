package com.example.demo.dto.Account.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {

    @NotBlank(message = "Nhập mật khẩu hiện tại")
    private String currentPassword;

    @NotBlank(message = "Nhập mật khẩu mới")
    private String newPassword;

    @NotBlank(message = "Nhập lại mật khẩu mới")
    private String confirmPassword;
}
