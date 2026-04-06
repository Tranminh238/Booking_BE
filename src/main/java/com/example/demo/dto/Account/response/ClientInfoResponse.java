package com.example.demo.dto.Account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientInfoResponse {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
