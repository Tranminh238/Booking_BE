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
    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerClient(RegistRequest registRequest) {
        if(userRepository.findByUsername(registRequest.getEmail()).isPresent()){
            throw new hotelException("User đã tồn tại");
        }
        Account userEntity = Account.builder()
                .username(registRequest.getEmail())
                .password(passwordEncoder.encode(registRequest.getPassword()))
                .role("CLIENT")
                .build();
        Account user = userRepository.save(userEntity);

        User client = User.builder()
                .userId(user.getId())
                .email(registRequest.getEmail())
                .firstName(registRequest.getFirstName())
                .lastName(registRequest.getLastName())
                .build();
        usersRepository.save(client);
    }

    @Transactional
    public void registerPartner(RegistRequest registRequest) {
        if(userRepository.findByUsername(registRequest.getEmail()).isPresent()){
            throw new hotelException("User đã tồn tại");
        }
        Account userEntity = Account.builder()
                .username(registRequest.getEmail())
                .password(passwordEncoder.encode(registRequest.getPassword()))
                .role("PARTNER")
                .build();
        Account user = userRepository.save(userEntity);

        User partner = User.builder()
                .userId(user.getId())
                .email(registRequest.getEmail())
                .firstName(registRequest.getFirstName())
                .lastName(registRequest.getLastName())
                .build();
        usersRepository.save(partner);
    }

    @Transactional
    public void editInfo(ClientEdditInfoRequest request){
        User client = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new hotelException("Client not found by id: " + request.getUserId()));
        Account user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new hotelException("User not found by id: " + request.getUserId()));

        user.setUsername(request.getEmail());
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);
        usersRepository.save(client);
    }

}
