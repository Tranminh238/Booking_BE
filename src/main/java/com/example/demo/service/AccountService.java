package com.example.demo.service;

import com.example.demo.dto.Account.request.RegistRequest;
import com.example.demo.dto.Account.request.ChangePassword;
import com.example.demo.dto.User.request.ClientEdditInfoRequest;
import com.example.demo.dto.User.response.ClientInfoResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.Account;
import com.example.demo.exception.hotelException;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UsersRepository usersRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

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
    @Transactional
    public void changePassword(String email, ChangePassword request) {

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() ->
                        new hotelException("Không tìm thấy user"));

        Account account = accountRepository.findByUsername(email)
                .orElseThrow(() ->
                        new hotelException("Tài khoản không tồn tại"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                account.getPassword())) {

            throw new hotelException("Mật khẩu hiện tại không chính xác");
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new hotelException("Xác nhận mật khẩu không khớp");
        }

        if (request.getCurrentPassword()
                .equals(request.getNewPassword())) {

            throw new hotelException(
                    "Mật khẩu mới không được trùng mật khẩu cũ");
        }

        account.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        accountRepository.save(account);
    }
    private String generateOtp() {
        // Tạo mã OTP 6 chữ số
        return String.valueOf((int) (Math.random() * 900000 + 100000));
    }
    public void requestOtp(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() ->
                        new hotelException("Không tìm thấy user"));
        Account account = accountRepository.findByUsername(email)
                .orElseThrow(() ->
                        new hotelException("Tài khoản không tồn tại"));
        String otp = generateOtp();

        account.setOtp(otp);
        account.setOtpCreatedAt(LocalDateTime.now().plusMinutes(5));
        accountRepository.save(account);

        emailService.sendOtpEmail(email, otp);
    }
    @Transactional
public void verifyOtp(String email, String otp) {
    Account account = accountRepository.findByUsername(email)
            .orElseThrow(() ->
                    new hotelException("Tài khoản không tồn tại"));

    if (!otp.equals(account.getOtp())) {
        throw new hotelException("Mã OTP không chính xác");
    }

    if (LocalDateTime.now().isAfter(account.getOtpCreatedAt())) {
        throw new hotelException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới");
    }
    account.setOtp(null);
    account.setOtpCreatedAt(null);
    accountRepository.save(account);
}
@Transactional
public void resetPassword(String email, String otp, String newPassword) {
    verifyOtp(email, otp);

    Account account = accountRepository.findByUsername(email)
            .orElseThrow(() ->
                    new hotelException("Tài khoản không tồn tại"));

    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);
}
}
