package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendHotelApprovedEmail(String toEmail, String hotelName,
                                       String address, String city,
                                       String country, int star) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Khách sạn của bạn đã được duyệt!");
            helper.setText(buildEmailContent(hotelName, address, city, country, star), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }

    private String buildEmailContent(String hotelName, String address,
                                     String city, String country, int star) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
              <div style="background-color: #4CAF50; padding: 24px; text-align: center;">
                <h1 style="color: white; margin: 0;">Khách sạn đã được duyệt!</h1>
              </div>
              <div style="padding: 24px;">
                <p>Xin chào,</p>
                <p>Khách sạn của bạn đã được <strong>phê duyệt thành công</strong> trên hệ thống. Dưới đây là thông tin chi tiết:</p>
                <table style="width: 100%%; border-collapse: collapse; margin-top: 16px;">
                  <tr style="background-color: #f5f5f5;">
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Tên khách sạn</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Trạng thái</td>
                    <td style="padding: 10px; border: 1px solid #ddd; color: #4CAF50;"><strong>✅ Đã duyệt</strong></td>
                  </tr>
                  <tr style="background-color: #f5f5f5;">
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Địa chỉ</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Thành phố</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                  </tr>
                  <tr style="background-color: #f5f5f5;">
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Quốc gia</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Hạng sao</td>
                    <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                  </tr>
                </table>
                <p style="margin-top: 24px;">Cảm ơn bạn đã đăng ký. Khách sạn của bạn hiện đã hiển thị trên nền tảng.</p>
              </div>
              <div style="background-color: #f5f5f5; padding: 16px; text-align: center; font-size: 12px; color: #999;">
                © 2025 Hotel Booking System
              </div>
            </div>
            """.formatted(hotelName, address, city, country, "⭐".repeat(star));
    }
}