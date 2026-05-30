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

    public void sendBookingConfirmationEmail(
            String toEmail,
            String customerName,
            Long bookingId,
            String hotelName,
            String hotelCity,
            String checkIn,
            String checkOut,
            int totalPrice) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác nhận đặt phòng thành công - Mã #" + bookingId);
            helper.setText(
                buildBookingConfirmationEmailContent(
                    customerName, bookingId, hotelName, hotelCity, checkIn, checkOut, totalPrice),
                true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email xác nhận đặt phòng: " + e.getMessage());
        }
    }

    private String buildBookingConfirmationEmailContent(
            String customerName,
            Long bookingId,
            String hotelName,
            String hotelCity,
            String checkIn,
            String checkOut,
            int totalPrice) {
        String formattedPrice = String.format("%,d VND", totalPrice).replace(",", ".");
        return """
            <div style="font-family: Arial, sans-serif; max-width: 620px; margin: auto; border: 1px solid #e0e0e0; border-radius: 10px; overflow: hidden;">
              <div style="background: linear-gradient(135deg, #1a73e8, #0d47a1); padding: 28px; text-align: center;">
                <h1 style="color: white; margin: 0; font-size: 22px;"> Đặt Phòng Thành Công!</h1>
                <p style="color: #cce0ff; margin: 8px 0 0;">Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi.</p>
              </div>
              <div style="padding: 28px;">
                <p style="font-size: 15px;">Xin chào <strong>%s</strong>,</p>
                <p>Thanh toán của bạn đã được xác nhận. Dưới đây là thông tin chi tiết đặt phòng:</p>
                <table style="width: 100%%; border-collapse: collapse; margin-top: 16px; font-size: 14px;">
                  <tr style="background-color: #f0f4ff;">
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold; width: 40%%;">Mã đặt phòng</td>
                    <td style="padding: 12px; border: 1px solid #dde; color: #1a73e8; font-weight: bold;">#%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Khách sạn</td>
                    <td style="padding: 12px; border: 1px solid #dde;">%s</td>
                  </tr>
                  <tr style="background-color: #f0f4ff;">
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Thành phố</td>
                    <td style="padding: 12px; border: 1px solid #dde;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Ngày nhận phòng</td>
                    <td style="padding: 12px; border: 1px solid #dde;">%s</td>
                  </tr>
                  <tr style="background-color: #f0f4ff;">
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Ngày trả phòng</td>
                    <td style="padding: 12px; border: 1px solid #dde;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Tổng thanh toán</td>
                    <td style="padding: 12px; border: 1px solid #dde; color: #e53935; font-size: 16px; font-weight: bold;">%s</td>
                  </tr>
                  <tr style="background-color: #f0f4ff;">
                    <td style="padding: 12px; border: 1px solid #dde; font-weight: bold;">Trạng thái</td>
                    <td style="padding: 12px; border: 1px solid #dde; color: #2e7d32;"><strong>✅ Đã thanh toán</strong></td>
                  </tr>
                </table>
                <p style="margin-top: 24px; color: #555;">Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email hỗ trợ.</p>
                <p style="color: #555;">Chúc bạn có một kỳ nghỉ tuyệt vời! </p>
              </div>
              <div style="background-color: #f5f5f5; padding: 16px; text-align: center; font-size: 12px; color: #999;">
                © 2025 Hotel Booking System
              </div>
            </div>
            """.formatted(
                customerName, bookingId, hotelName, hotelCity, checkIn, checkOut, formattedPrice);
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
                    <td style="padding: 10px; border: 1px solid #ddd; color: #4CAF50;"><strong> Đã duyệt</strong></td>
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
            """.formatted(hotelName, address, city, country, "".repeat(star));
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(" Mã OTP xác minh quên mật khẩu");
            helper.setText(buildOtpEmailContent(otp), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email OTP: " + e.getMessage());
        }
    }

    private String buildOtpEmailContent(String otp) {
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
              <div style="background: linear-gradient(135deg, #1a73e8, #0d47a1); padding: 24px; text-align: center;">
                <h1 style="color: white; margin: 0; font-size: 20px;"> Xác Minh Quên Mật Khẩu</h1>
              </div>
              <div style="padding: 24px;">
                <p>Xin chào,</p>
                <p>Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng sử dụng mã OTP dưới đây để hoàn tất quá trình xác minh:</p>
                <div style="text-align: center; margin: 24px 0;">
                  <span style="font-size: 32px; font-weight: bold; letter-spacing: 6px; color: #1a73e8; padding: 10px 20px; background-color: #f0f4ff; border-radius: 8px; border: 1px dashed #1a73e8;">%s</span>
                </div>
                <p style="color: #e53935; font-size: 13px; font-weight: bold;">Mã OTP này có hiệu lực trong vòng 5 phút.</p>
                <p style="color: #666; font-size: 13px;">Vui lòng không chia sẻ mã này với bất kỳ ai để bảo vệ tài khoản của bạn.</p>
                <p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>
              </div>
              <div style="background-color: #f5f5f5; padding: 16px; text-align: center; font-size: 12px; color: #999;">
                © 2026 Hotel Booking System
              </div>
            </div>
            """.formatted(otp);
    }
}