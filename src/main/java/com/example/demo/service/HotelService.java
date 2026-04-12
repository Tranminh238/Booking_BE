package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Util.FileUpLoadUtil;
import com.example.demo.dto.CloudinaryResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final ImageRepository imageRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void createHotel(HotelForm form, List<MultipartFile> imageFiles, List<MultipartFile> policyFiles) {
        if (hotelRepository.existsByName(form.getName())) {
            throw new RuntimeException("Tên khách sạn đã tồn tại");
        }

        Hotel hotel = Hotel.builder()
                .userId(form.getUserId())
                .name(form.getName())
                .star(form.getStar() != null ? form.getStar() : 0)
                .status(1)
                .description(form.getDescription())
                .checkin_time_start(form.getCheckin_time_start())
                .checkin_time_end(form.getCheckin_time_end())
                .checkout_time_start(form.getCheckout_time_start())
                .checkout_time_end(form.getCheckout_time_end())
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

        // Upload ảnh lên Cloudinary
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) continue;
                FileUpLoadUtil.assertAllowedExtention(file, FileUpLoadUtil.IMAGE_PATTERN);
                String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
                CloudinaryResponse uploaded = cloudinaryService.uploadFile(file, "hotel_" + hotel.getId() + "_" + fileName);
                Image image = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.HOTEL)
                        .imageUrl(uploaded.getUrl())
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        // Upload policy files lên Cloudinary
        if (policyFiles != null && !policyFiles.isEmpty()) {
            for (MultipartFile file : policyFiles) {
                if (file == null || file.isEmpty()) continue;
                String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
                CloudinaryResponse uploaded = cloudinaryService.uploadFile(file, "hotel_policy_" + hotel.getId() + "_" + fileName);
                Image policyImg = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.POLICY)
                        .imageUrl(uploaded.getUrl())
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(policyImg);
            }
        }
    }

    @Transactional
    public BaseResponse updateHotel(Long id, HotelForm form, List<MultipartFile> imageFiles, List<MultipartFile> policyFiles) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        if (form.getName() != null && !form.getName().trim().isEmpty()
                && !hotel.getName().equals(form.getName())) {
            if (hotelRepository.existsByName(form.getName())) {
                return new BaseResponse(400, "Hotel name already exists", null);
            }
            hotel.setName(form.getName());
        }

        if (form.getStar() != null)
            hotel.setStar(form.getStar());
        if (form.getStatus() != null)
            hotel.setStatus(form.getStatus());
        if (form.getDescription() != null)
            hotel.setDescription(form.getDescription());
        hotel.setUpdated_at(LocalDateTime.now());
        hotelRepository.save(hotel);

        // Cập nhật địa chỉ
        if (form.getDistrict() != null || form.getCity() != null || form.getCountry() != null) {
            HotelAddress address = hotelAddressRepository.findByHotelId(hotel.getId())
                    .orElse(HotelAddress.builder().hotelId(hotel.getId()).build());
            if (form.getDistrict() != null)
                address.setDistrict(form.getDistrict());
            if (form.getCity() != null)
                address.setCity(form.getCity());
            if (form.getCountry() != null)
                address.setCountry(form.getCountry());
            hotelAddressRepository.save(address);
        }

        // Cập nhật ảnh: xóa cũ → upload & thêm mới
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageRepository.deleteByRefIdAndRefType(hotel.getId(), RefType.HOTEL);
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) continue;
                FileUpLoadUtil.assertAllowedExtention(file, FileUpLoadUtil.IMAGE_PATTERN);
                String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
                CloudinaryResponse uploaded = cloudinaryService.uploadFile(file, "hotel_" + hotel.getId() + "_" + fileName);
                Image image = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.HOTEL)
                        .imageUrl(uploaded.getUrl())
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        // Cập nhật policy files: xóa cũ → upload & thêm mới
        if (policyFiles != null && !policyFiles.isEmpty()) {
            imageRepository.deleteByRefIdAndRefType(hotel.getId(), RefType.POLICY);
            for (MultipartFile file : policyFiles) {
                if (file == null || file.isEmpty()) continue;
                String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
                CloudinaryResponse uploaded = cloudinaryService.uploadFile(file, "hotel_policy_" + hotel.getId() + "_" + fileName);
                Image policyImg = Image.builder()
                        .refId(hotel.getId())
                        .refType(RefType.POLICY)
                        .imageUrl(uploaded.getUrl())
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(policyImg);
            }
        }

        return new BaseResponse(200, "Hotel updated successfully", mapToResponse(hotel));
    }

    public BaseResponse browseHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
        hotel.setStatus(2);
        hotelRepository.save(hotel);
        return new BaseResponse(200, "Hotel browsed successfully", mapToResponse(hotel));
    }

    @Transactional
    public void deleteHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
        hotel.setStatus(0);
        hotelRepository.save(hotel);
    }

    @Transactional
    public Page<HotelResponse> getAllHotels(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> result = hotelRepository.findHotelactive(pageable);
        return result.map(this::mapToResponse);
    }

    public Page<HotelResponse> getHotelByCity(String city, int page, int size) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City không được để trống");
        }
        String normalizedCity = city.trim().toLowerCase();
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> result = hotelRepository.findHotelByCity(normalizedCity, pageable);
        return result.map(this::mapToResponse);
    }

    public List<HotelResponse> getHotelByUserId(Long userId) {
        List<Hotel> result = hotelRepository.findHotelByUserId(userId);
        return result.stream().map(this::mapToResponse).toList();
    }

    public HotelResponse getHotelDetail(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
        return mapToResponse(hotel);
    }

    private HotelResponse mapToResponse(Hotel hotel) {
        String address = hotelAddressRepository.findByHotelId(hotel.getId())
                .map(a -> a.getDistrict() + ", " + a.getCity() + ", " + a.getCountry())
                .orElse(null);

        List<String> images = imageRepository.findByRefIdAndRefType(hotel.getId(), RefType.HOTEL)
                .stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        List<String> policyUrls = imageRepository.findByRefIdAndRefType(hotel.getId(), RefType.POLICY)
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
                .policy_url(policyUrls)
                .checkin_time_start(hotel.getCheckin_time_start())
                .checkin_time_end(hotel.getCheckin_time_end())
                .checkout_time_start(hotel.getCheckout_time_start())
                .checkout_time_end(hotel.getCheckout_time_end())
                .created_at(hotel.getCreated_at())
                .updated_at(hotel.getUpdated_at())
                .build();
    }

}
