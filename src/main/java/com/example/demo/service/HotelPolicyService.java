package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.HotelPolicy;
import com.example.demo.repository.HotelPolicyRepository;
import com.example.demo.dto.Hotel.request.HotelForm;

import java.util.Optional;

@Service
public class HotelPolicyService {

    @Autowired
    private HotelPolicyRepository hotelPolicyRepository;

    /**
     * Tạo mới chính sách cho khách sạn sau khi tạo khách sạn
     */
    @Transactional
    public HotelPolicy createPolicy(Long hotelId, HotelForm form) {
        HotelPolicy policy = HotelPolicy.builder()
                .hotelId(hotelId)
                .identificationDocuments(form.getIdentificationDocuments())
                .checkInInstructions(form.getCheckInInstructions())
                .smokePolicy(form.getSmokePolicy())
                .petPolicy(form.getPetPolicy())
                .isRefund(form.getIsRefund())
                .minDateRefund(form.getMinDateRefund())
                .refundPercentage(form.getRefundPercentage())
                .build();
        return hotelPolicyRepository.save(policy);
    }

    /**
     * Cập nhật hoặc tạo mới chính sách khách sạn
     */
    @Transactional
    public HotelPolicy updatePolicy(Long hotelId, HotelForm form) {
        HotelPolicy policy = hotelPolicyRepository.findByHotelId(hotelId)
                .orElse(HotelPolicy.builder().hotelId(hotelId).build());

        if (form.getIdentificationDocuments() != null)
            policy.setIdentificationDocuments(form.getIdentificationDocuments());
        if (form.getCheckInInstructions() != null)
            policy.setCheckInInstructions(form.getCheckInInstructions());
        if (form.getSmokePolicy() != null)
            policy.setSmokePolicy(form.getSmokePolicy());
        if (form.getPetPolicy() != null)
            policy.setPetPolicy(form.getPetPolicy());
        if (form.getIsRefund() != null)
            policy.setIsRefund(form.getIsRefund());
        if (form.getMinDateRefund() != null)
            policy.setMinDateRefund(form.getMinDateRefund());
        if (form.getRefundPercentage() != null)
            policy.setRefundPercentage(form.getRefundPercentage());

        return hotelPolicyRepository.save(policy);
    }

    /**
     * Lấy chính sách theo hotelId
     */
    public Optional<HotelPolicy> getPolicyByHotelId(Long hotelId) {
        return hotelPolicyRepository.findByHotelId(hotelId);
    }

    /**
     * Xoá chính sách theo hotelId
     */
    @Transactional
    public void deletePolicyByHotelId(Long hotelId) {
        hotelPolicyRepository.findByHotelId(hotelId)
                .ifPresent(hotelPolicyRepository::delete);
    }
}
