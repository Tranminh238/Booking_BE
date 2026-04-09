package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.Hotel.request.HotelForm;
import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.dto.base.BaseResponse;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.HotelAddress;
import com.example.demo.entity.HotelAmenities;
import com.example.demo.entity.Image;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.repository.HotelAddressRepository;
import com.example.demo.repository.HotelAmenitiesRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final ImageRepository imageRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;

    @Transactional
    public BaseResponse createHotel(HotelForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            return new BaseResponse(400, "Hotel name is required", null);
        }
        if (hotelRepository.existsByName(form.getName())) {
            return new BaseResponse(400, "Hotel name already exists", null);
        }

        Hotel hotel = Hotel.builder()
                .userId(form.getUserId())
                .name(form.getName())
                .star(form.getStar() != null ? form.getStar() : 0)
                .status(1)
                .description(form.getDescription())
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        hotelRepository.save(hotel);

        // Lưu địa chỉ
        if (form.getDistrict() != null || form.getCity() != null || form.getCountry() != null) {
            HotelAddress address = HotelAddress.builder()
                    .hotelId(hotel.getId())
                    .district(form.getDistrict())
                    .city(form.getCity())
                    .country(form.getCountry())
                    .build();
            hotelAddressRepository.save(address);
        }
        if (form.getAmenityIds() != null && !form.getAmenityIds().isEmpty()) {
            for (Long amenityId : form.getAmenityIds()) {
                HotelAmenities ha = HotelAmenities.builder()
                        .hotelId(hotel.getId())
                        .amenityId(amenityId)
                        .build();
                hotelAmenitiesRepository.save(ha);
            }
        }
        // Lưu ảnh
        if (form.getImageUrls() != null && !form.getImageUrls().isEmpty()) {
            for (String url : form.getImageUrls()) {
                Image image = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.HOTEL)
                        .imageUrl(url)
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        return new BaseResponse(200, "Hotel created successfully", mapToResponse(hotel));
    }

    @Transactional
    public BaseResponse updateHotel(Long id, HotelForm form) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        if (form.getName() != null && !form.getName().trim().isEmpty()
                && !hotel.getName().equals(form.getName())) {
            if (hotelRepository.existsByName(form.getName())) {
                return new BaseResponse(400, "Hotel name already exists", null);
            }
            hotel.setName(form.getName());
        }

        if (form.getStar() != null) hotel.setStar(form.getStar());
        if (form.getStatus() != null) hotel.setStatus(form.getStatus());
        if (form.getDescription() != null) hotel.setDescription(form.getDescription());
        hotel.setUpdated_at(LocalDateTime.now());
        hotelRepository.save(hotel);

        // Cập nhật địa chỉ
        if (form.getDistrict() != null || form.getCity() != null || form.getCountry() != null) {
            HotelAddress address = hotelAddressRepository.findByHotelId(hotel.getId())
                    .orElse(HotelAddress.builder().hotelId(hotel.getId()).build());
            if (form.getDistrict() != null) address.setDistrict(form.getDistrict());
            if (form.getCity() != null) address.setCity(form.getCity());
            if (form.getCountry() != null) address.setCountry(form.getCountry());
            hotelAddressRepository.save(address);
        }

        // Cập nhật ảnh: xóa cũ → thêm mới
        if (form.getImageUrls() != null) {
            imageRepository.deleteByRefIdAndRefType(hotel.getId(), RefType.HOTEL);
            for (String url : form.getImageUrls()) {
                Image image = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.HOTEL)
                        .imageUrl(url)
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        return new BaseResponse(200, "Hotel updated successfully", mapToResponse(hotel));
    }

    @Transactional
    public BaseResponse deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        hotel.setStatus(0);
        hotelRepository.save(hotel);

        return new BaseResponse(200, "Hotel deleted successfully", null);
    }

    @Transactional
    public BaseResponse getAllHotels() {
        List<HotelResponse> result = hotelRepository.findHotelactive()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new BaseResponse(200, "Success", result);
    }

    public BaseResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return new BaseResponse(200, "Success", mapToResponse(hotel));
    }

    private HotelResponse mapToResponse(Hotel hotel) {
        String address = hotelAddressRepository.findByHotelId(hotel.getId())
                .map(a -> a.getDistrict() + ", " + a.getCity() + ", " + a.getCountry())
                .orElse(null);

        List<String> images = imageRepository.findByRefIdAndRefType(hotel.getId(), RefType.HOTEL)
                .stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .star(hotel.getStar())
                .rating_avg(hotel.getRating_avg())
                .status(hotel.getStatus())
                .description(hotel.getDescription())
                .address(address)
                .images(images)
                .created_at(hotel.getCreated_at())
                .updated_at(hotel.getUpdated_at())
                .build();
    }

}
