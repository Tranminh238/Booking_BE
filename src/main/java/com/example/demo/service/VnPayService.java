package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.config.VnPayConfig;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Payment;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    public String createPaymentUrl(Long bookingId, HttpServletRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        long amount = booking.getTotalPrice() * 100L;
        String vnp_TxnRef = VnPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VnPayConfig.getIpAddress(request);
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don dat phong " + bookingId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Save or update Payment
        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(new Payment());
        payment.setBookingId(bookingId);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod("VNPAY");
        payment.setStatus(0); // 0 = pending
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
        paymentRepository.save(payment);

        return paymentUrl;
    }

    public int resultPayment(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = VnPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");

            String bookingIdStr = vnp_OrderInfo == null ? "" : vnp_OrderInfo.replaceAll("\\D+", "");
            if (!bookingIdStr.isEmpty()) {
                Long bookingId = Long.parseLong(bookingIdStr);

                if ("00".equals(vnp_ResponseCode)) {
                    // Gọi bookingService.payment() để cập nhật booking + payment + gửi email
                    bookingService.payment(true, bookingId);
                    return 1;
                } else {
                    // Thanh toán thất bại: cập nhật payment và booking thành cancelled
                    bookingService.payment(false, bookingId);
                    return 0;
                }
            }
            return "00".equals(vnp_ResponseCode) ? 1 : 0;
        } else {
            return -1; // Invalid checksum
        }
    }
}
