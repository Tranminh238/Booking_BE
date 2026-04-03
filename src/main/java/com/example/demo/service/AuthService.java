package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.auth.request.AuthRequest;
import com.example.demo.dto.auth.response.AuthResponse;
import com.example.demo.dto.base.BaseResponse;
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

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public BaseResponse login(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
            throw new hotelException("Tài khoản không tồn tại");
        }
        Optional<Account> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            boolean checkPassword = passwordEncoder.matches(request.getPassword(), user.get().getPassword());
            if (!checkPassword) {
                throw new hotelException("Mật khẩu không đúng!");
            }
            String role = userRepository.findUserRoleByUsername(user.get().getUsername());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(role));
            user.get().setRole(role);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String jwtToken = jwtService.createToken(authentication);

            Optional<com.example.demo.entity.User> clientOpt = clientRepository.findByUserId(user.get().getId());
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

}
