package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Util.FileUpLoadUtil;
import com.example.demo.dto.CloudinaryResponse;
import com.example.demo.repository.AmenityRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.RoomTypeRepository;
import com.example.demo.repository.RoomAmenitiesRepository;
import com.example.demo.repository.RoomAvailabilityRepository;

import jakarta.transaction.Transactional;
import com.example.demo.dto.Room.request.RoomForm;
import com.example.demo.dto.Room.response.RoomResponse;
import com.example.demo.entity.RoomAvailability;
import com.example.demo.entity.Image;
import com.example.demo.entity.Room;
import com.example.demo.entity.RoomAmenities;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.dto.base.BaseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final ImageRepository imageRepository;
    private final RoomAmenitiesRepository roomAmenitiesRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final RoomAvailibabilityService roomAvailibabilityService;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public BaseResponse createRoom(RoomForm form, List<MultipartFile> imageFiles) {
        Room room = Room.builder()
                .pricePerNight(form.getPricePerNight())
                .capacity(form.getCapacity())
                .quantity(form.getQuantity())
                .area(form.getArea())
                .status(form.getStatus())
                .description(form.getDescription())
                .created_at(LocalDateTime.now())
                .build();
        roomRepository.save(room);

        if (form.getAmenityIds() != null && !form.getAmenityIds().isEmpty()) {
            for (Long amenityId : form.getAmenityIds()) {
                RoomAmenities roomAmenities = RoomAmenities.builder()
                        .roomId(room.getId())
                        .amenityId(amenityId)
                        .build();
                roomAmenitiesRepository.save(roomAmenities);
            }
        }

        roomAvailibabilityService.generateAvailabilityForRoom(room, 365);

        // Upload ảnh lên Cloudinary
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) continue;
                FileUpLoadUtil.assertAllowedExtention(file, FileUpLoadUtil.IMAGE_PATTERN);
                String fileName = FileUpLoadUtil.getFileName(file.getOriginalFilename());
                CloudinaryResponse uploaded = cloudinaryService.uploadFile(file, "room_" + room.getId() + "_" + fileName);
                Image image = Image.builder()
                        .refId(room.getId())
                        .refType(RefType.ROOM)
                        .imageUrl(uploaded.getUrl())
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        return new BaseResponse(200, "Success", null);
    }

    
}
