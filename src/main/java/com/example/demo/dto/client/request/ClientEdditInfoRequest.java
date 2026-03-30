package com.example.demo.dto.client.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientEdditInfoRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String phoneNumber;
    }
