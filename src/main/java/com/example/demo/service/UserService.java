package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.AccountRepository;
import com.example.demo.dto.Account.request.AuthRequest;
import com.example.demo.dto.Account.response.AuthResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.dto.User.response.UserResponse;
import com.example.demo.exception.hotelException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AccountRepository accountRepository;
    private final UsersRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public BaseResponse login(AuthRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isEmpty()) {
            throw new hotelException("Tài khoản không tồn tại");
        }
        Optional<Account> user = accountRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            boolean checkPassword = passwordEncoder.matches(request.getPassword(), user.get().getPassword());
            if (!checkPassword) {
                throw new hotelException("Mật khẩu không đúng!");
            }
            String role = accountRepository.findUserRoleByUsername(user.get().getUsername());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            user.get().setRole(role);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String jwtToken = jwtService.createToken(authentication);

            Optional<com.example.demo.entity.User> clientOpt = userRepository.findByUserId(user.get().getId());
            String fName = clientOpt.isPresent() ? clientOpt.get().getFirstName() : "";
            String lName = clientOpt.isPresent() ? clientOpt.get().getLastName() : "";

            AuthResponse response = AuthResponse.builder()
                    .token(jwtToken)
                    .userId(user.get().getId())
                    .role(user.get().getRole())
                    .firstName(fName)
                    .lastName(lName)
                    .build();
            return new BaseResponse(200, "Success", response);
        } else {
            return new BaseResponse(500, "Tài khoản nhập không chính xác", null);
        }
    }

    public BaseResponse loginPartner(AuthRequest request) {
        if (accountRepository.findByUsername(request.getUsername()).isEmpty()) {
            throw new hotelException("Tài khoản không tồn tại");
        }
        Optional<Account> user = accountRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            boolean checkPassword = passwordEncoder.matches(request.getPassword(), user.get().getPassword());
            if (!checkPassword) {
                throw new hotelException("Mật khẩu không đúng!");
            }
            String role = accountRepository.findUserRoleByUsername(user.get().getUsername());
            if (!role.equals("PARTNER") && !role.equals("ADMIN")) {
                throw new hotelException("Tài khoản không phải là Partner!");
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            user.get().setRole(role);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String jwtToken = jwtService.createToken(authentication);

            Optional<com.example.demo.entity.User> clientOpt = userRepository.findByUserId(user.get().getId());
            String fName = clientOpt.isPresent() ? clientOpt.get().getFirstName() : "";
            String lName = clientOpt.isPresent() ? clientOpt.get().getLastName() : "";

            AuthResponse response = AuthResponse.builder()
                    .token(jwtToken)
                    .userId(user.get().getId())
                    .role(user.get().getRole())
                    .firstName(fName)
                    .lastName(lName)
                    .build();
            return new BaseResponse(200, "Success", response);
        } else {
            return new BaseResponse(500, "Tài khoản nhập không chính xác", null);
        }
    }

    public List<UserResponse> getAllUser() {
        return userRepository.findAllUser();
    }
}
