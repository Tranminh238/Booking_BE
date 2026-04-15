package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
                .hotelId(form.getHotelId())
                .roomTypeId(form.getRoomTypeId())
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

    @Transactional
    public BaseResponse updateRoom(Long roomId, RoomForm form, List<MultipartFile> imageFiles) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (form.getHotelId() != null) room.setHotelId(form.getHotelId());
        if (form.getRoomTypeId() != null) room.setRoomTypeId(form.getRoomTypeId());
        if (form.getPricePerNight() != null) room.setPricePerNight(form.getPricePerNight());
        if (form.getCapacity() != null) room.setCapacity(form.getCapacity());
        if (form.getQuantity() != null) room.setQuantity(form.getQuantity());
        if (form.getArea() != null) room.setArea(form.getArea());
        if (form.getStatus() != null) room.setStatus(form.getStatus());
        if (form.getDescription() != null) room.setDescription(form.getDescription());
        room.setUpdated_at(LocalDateTime.now());
        roomRepository.save(room);

        // Update amenities
        if (form.getAmenityIds() != null) {
            // Delete old
            List<RoomAmenities> olds = roomAmenitiesRepository.findAll().stream()
                .filter(ra -> ra.getRoomId().equals(room.getId()))
                .collect(Collectors.toList());
            roomAmenitiesRepository.deleteAll(olds);
            
            // Save new
            for (Long amenityId : form.getAmenityIds()) {
                RoomAmenities roomAmenities = RoomAmenities.builder()
                        .roomId(room.getId())
                        .amenityId(amenityId)
                        .build();
                roomAmenitiesRepository.save(roomAmenities);
            }
        }

        // Update images
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageRepository.deleteByRefIdAndRefType(room.getId(), RefType.ROOM);
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

        return new BaseResponse(200, "Room updated successfully", null);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setStatus(0);
        room.setUpdated_at(LocalDateTime.now());
        roomAvailabilityRepository.deleteByRoomId(roomId);
        roomRepository.save(room);
    }

    public RoomResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        return mapToResponse(room);
    }

    public List<RoomResponse> getRoomsByHotelId(Long hotelId) {
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return rooms.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public Page<RoomResponse> getAllRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> rooms = roomRepository.findAll(pageable);
        return rooms.map(this::mapToResponse);
    }

    private RoomResponse mapToResponse(Room room) {
        List<Long> amenities = roomAmenitiesRepository.findAll().stream()
                .filter(ra -> ra.getRoomId().equals(room.getId()))
                .map(RoomAmenities::getAmenityId)
                .collect(Collectors.toList());

        List<String> images = imageRepository.findByRefIdAndRefType(room.getId(), RefType.ROOM)
                .stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        return new RoomResponse(
                room.getId(),
                room.getHotelId(),
                room.getRoomTypeId(),
                room.getPricePerNight(),
                room.getCapacity(),
                room.getQuantity(),
                room.getArea(),
                room.getStatus(),
                room.getDescription(),
                amenities,
                images
        );
    }
}
