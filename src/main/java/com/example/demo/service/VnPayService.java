package com.example.demo.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.stereotype.Service;

import com.example.demo.config.VnPayConfig;
import com.example.demo.entity.Booking;
import com.example.demo.repository.BookingRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VnPayService {
    
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public String payment(Long bookingId, HttpServletRequest req) throws Exception {

        // Lấy thông tin booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Không tìm thấy đơn đặt phòng"));
        
        // Status = 1 (PENDING) là trạng thái đang chờ thanh toán/chờ xác nhận
        if(booking.getStatus() != 1) {
            throw new Exception("Đơn đặt phòng đã được thanh toán hoặc không ở trạng thái chờ");
        }

        long amount = booking.getTotalPrice().longValue() * 100;

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = "Thanh toan don dat phong " + bookingId;
        String orderType = "other";
        String vnp_TxnRef = String.valueOf(bookingId); // Dùng bookingId làm mã giao dịch để dễ mapping
        String vnp_IpAddr = VnPayConfig.getIpAddress(req);
        if ("0:0:0:0:0:0:0:1".equals(vnp_IpAddr)) {
            vnp_IpAddr = "127.0.0.1";
        }
        String vnp_TmnCode = VnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Ngày tạo + hết hạn
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Build secure hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                // Mã hóa URL cả value theo đúng chuẩn VNPay v2.1.0
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
        }

        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return VnPayConfig.vnp_PayUrl + "?" + query;
    }

    public int resultPayment(HttpServletRequest request) throws Exception {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String key = params.nextElement();
            String value = request.getParameter(key);
            if (value != null && !value.isEmpty()) {
                fields.put(key, value);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VnPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            String responseCode = request.getParameter("vnp_ResponseCode");
            String txnStatus = request.getParameter("vnp_TransactionStatus");
            if ("00".equals(responseCode) && "00".equals(txnStatus)) {
                // vnp_TxnRef chính là bookingId
                Long bookingId = Long.valueOf(request.getParameter("vnp_TxnRef"));
                
                // Xác nhận trạng thái booking (Chuyển thành CONFIRMED = 2)
                bookingService.confirmBooking(bookingId);
                
                return 1; // Thành công
            }
            return 0; // Thất bại (Ví dụ: khách hàng hủy thanh toán)
        }
        return -1; // Sai chữ ký kiểm tra (Bị can thiệp dữ liệu)
    }
}
