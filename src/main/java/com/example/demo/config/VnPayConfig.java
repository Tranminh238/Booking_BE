package com.example.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;

import jakarta.servlet.http.HttpServletRequest;

public class VnPayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
   public static String vnp_Returnurl = "http://localhost:8889/api/booking/payment/callback";
   public static String vnp_TmnCode = "7RHJZZQE";
   public static String vnp_HashSecret = "SIJIPX9NR5UHN659IYR6PST232ZCG6HG";
   public static String vnp_apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

   public static String hashAllFields(Map fields) {
       List fieldNames = new ArrayList(fields.keySet());
       Collections.sort(fieldNames);
       StringBuilder sb = new StringBuilder();
       Iterator itr = fieldNames.iterator();
       while (itr.hasNext()) {
           String fieldName = (String) itr.next();
           String fieldValue = (String) fields.get(fieldName);
           if ((fieldValue != null) && (fieldValue.length() > 0)) {
               sb.append(fieldName);
               sb.append("=");
               try {
                   sb.append(java.net.URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
               } catch (Exception e) {
                   sb.append(fieldValue);
               }
           }
           if (itr.hasNext()) {
               sb.append("&");
           }
       }
       return hmacSHA512(vnp_HashSecret,sb.toString());
   }
   public static String hmacSHA512(final String key, final String data) {
       try {

           if (key == null || data == null) {
               throw new NullPointerException();
           }
           final Mac hmac512 = Mac.getInstance("HmacSHA512");
           byte[] hmacKeyBytes = key.getBytes();
           final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
           hmac512.init(secretKey);
           byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
           byte[] result = hmac512.doFinal(dataBytes);
           StringBuilder sb = new StringBuilder(2 * result.length);
           for (byte b : result) {
               sb.append(String.format("%02x", b & 0xff));
           }
           return sb.toString();

       } catch (Exception ex) {
           return "";
       }
   }
   public static String getRandomNumber(int len) {
       Random rnd = new Random();
       String chars = "0123456789";
       StringBuilder sb = new StringBuilder(len);
       for (int i = 0; i < len; i++) {
           sb.append(chars.charAt(rnd.nextInt(chars.length())));
       }
       return sb.toString();
   }

   public static String getIpAddress(HttpServletRequest request) {
       String ipAdress;
       try {
           ipAdress = request.getHeader("X-FORWARDED-FOR");
           if (ipAdress == null) {
               ipAdress = request.getLocalAddr();
           }
       } catch (Exception e) {
           ipAdress = "Invalid IP:" + e.getMessage();
       }
       return ipAdress;
   }
}
