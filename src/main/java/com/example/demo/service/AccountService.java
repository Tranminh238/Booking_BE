package com.example.demo.service;

import com.example.demo.dto.Account.request.RegistRequest;
import com.example.demo.dto.User.request.ClientEdditInfoRequest;
import com.example.demo.dto.User.response.ClientInfoResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.Account;
import com.example.demo.exception.hotelException;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UsersRepository usersRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerClient(RegistRequest registRequest) {
        if (accountRepository.findByUsername(registRequest.getEmail()).isPresent()) {
            throw new hotelException("User đã tồn tại");
        }
        Account account = Account.builder()
                .username(registRequest.getEmail())
                .password(passwordEncoder.encode(registRequest.getPassword()))
                .role("CLIENT")
                .build();
        account = accountRepository.save(account);

        User client = User.builder()
                .userId(account.getId())
                .email(registRequest.getEmail())
                .firstName(registRequest.getFirstName())
                .lastName(registRequest.getLastName())
                .phoneNumber(registRequest.getPhoneNumber())
                .build();
        usersRepository.save(client);
    }

    @Transactional
    public void registerPartner(RegistRequest registRequest) {
        if (accountRepository.findByUsername(registRequest.getEmail()).isPresent()) {
            throw new hotelException("User đã tồn tại");
        }
        Account account = Account.builder()
                .username(registRequest.getEmail())
                .password(passwordEncoder.encode(registRequest.getPassword()))
                .role("PARTNER")
                .build();
        accountRepository.save(account);

        User partner = User.builder()
                .userId(account.getId())
                .email(registRequest.getEmail())
                .firstName(registRequest.getFirstName())
                .lastName(registRequest.getLastName())
                .phoneNumber(registRequest.getPhoneNumber())
                .build();
        usersRepository.save(partner);
    }

    @Transactional
    public void editInfo(ClientEdditInfoRequest request) {
        User client = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new hotelException("Client not found by id: " + request.getUserId()));

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setDateOfBirth(request.getDateOfBirth());
        client.setGender(request.getGender());
        client.setNationality(request.getNationality());
        usersRepository.save(client);
    }

    public ClientInfoResponse getClientInfo(Long userId) {
        User client = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new hotelException("Client not found by userId: " + userId));
        return ClientInfoResponse.builder()
                .userId(client.getUserId())
                .email(client.getEmail())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .dateOfBirth(client.getDateOfBirth())
                .gender(client.getGender())
                .nationality(client.getNationality())
                .build();
    }
}
