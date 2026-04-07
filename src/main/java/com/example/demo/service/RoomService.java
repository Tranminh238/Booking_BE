package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.repository.AmenityRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.RoomTypeRepository;

import jakarta.transaction.Transactional;

import com.example.demo.repository.RoomAmenitiesRepository;
import com.example.demo.dto.Room.request.RoomForm;
import com.example.demo.dto.Room.response.RoomResponse;
import com.example.demo.entity.HotelAmenities;
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

    @Transactional
    public BaseResponse createRoom(RoomForm form) {
        Room room = Room.builder()
                .pricePerNight(form.getPricePerNight())
                .capacity(form.getCapacity())
                .quantity(form.getQuantity())
                .area(form.getArea())
                .status(form.getStatus())
                .description(form.getDescription())
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

        if (form.getImageUrls() != null && !form.getImageUrls().isEmpty()) {
            for (String url : form.getImageUrls()) {
                Image image = Image.builder()
                        .refId(room.getId())
                        .refType(RefType.ROOM)
                        .imageUrl(url)
                        .createAt(LocalDateTime.now())
                        .build();
                imageRepository.save(image);
            }
        }

        return new BaseResponse(200, "Success", null);
    }
}
