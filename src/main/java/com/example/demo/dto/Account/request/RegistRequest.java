package com.example.demo.dto.Account.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistRequest {
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 8, message = "Password phải có ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "ConfirmPassword không được để trống")
    private String confirmPassword;

    @NotBlank(message = "FirstName không được để trống")
    private String firstName;

    @NotBlank(message = "LastName không được để trống")
    private String lastName;

    @NotBlank(message = "PhoneNumber không được để trống")
    private String phoneNumber;

}
