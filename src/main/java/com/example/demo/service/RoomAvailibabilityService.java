package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.RoomAvailability;
import com.example.demo.repository.RoomAvailabilityRepository;

import lombok.RequiredArgsConstructor;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.entity.Room;

@Service
@RequiredArgsConstructor
public class RoomAvailibabilityService {
    private final RoomAvailabilityRepository availabilityRepository;

    @Transactional
    public void generateAvailabilityForRoom(Room room, int days) {
        LocalDate today = LocalDate.now();

        List<RoomAvailability> list = new ArrayList<>();

        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);

            // tránh duplicate
            if (!availabilityRepository.existsByRoomIdAndDate(room.getId(), date)) {
                RoomAvailability ra = new RoomAvailability();
                ra.setRoomId(room.getId());
                ra.setDate(date);
                ra.setQuantityAvailable(room.getQuantity()); // tổng số phòng

                list.add(ra);
            }
        }
        availabilityRepository.saveAll(list);
    }

    @Transactional
    public void generateNextDay(Room room) {
        LocalDate nextDate = LocalDate.now().plusDays(365);

        if (!availabilityRepository.existsByRoomIdAndDate(room.getId(), nextDate)) {
            RoomAvailability ra = new RoomAvailability();
            ra.setRoomId(room.getId());
            ra.setDate(nextDate);
            ra.setQuantityAvailable(room.getQuantity());

            availabilityRepository.save(ra);
        }
    }
}
