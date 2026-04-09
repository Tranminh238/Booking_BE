package com.example.demo.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomAvailibabilityService;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutoFunction {
    private final RoomRepository roomRepository;
    private final RoomAvailibabilityService availabilityService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void job() {
        List<Room> rooms = roomRepository.findAll();

        for (Room room : rooms) {
            availabilityService.generateNextDay(room);
        }
    }
}
