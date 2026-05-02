package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.Booking.Request.BookingRequest;
import com.example.demo.dto.Booking.Response.BookingResponse;
import com.example.demo.dto.BookingDetail.Response.BookingDetailResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingDetail;
import com.example.demo.entity.Room;
import com.example.demo.entity.RoomAvailability;
import com.example.demo.entity.Payment;
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomAvailabilityRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest req) {
        if (req.getCheckInDate() == null || req.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Ngày check-in và check-out không được để trống");
        }
        if (!req.getCheckInDate().isBefore(req.getCheckOutDate())) {
            throw new IllegalArgumentException("Ngày check-in phải trước ngày check-out");
        }
        if (req.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày check-in không được là ngày trong quá khứ");
        }

        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại: id=" + req.getRoomId()));

        if (room.getStatus() == null || room.getStatus() == 0) {
            throw new RuntimeException("Phòng hiện không hoạt động");
        }

        int numRoom = req.getNumRoom() != null ? req.getNumRoom() : 1;

        long unavailableDays = roomAvailabilityRepository.countUnavailableDays(
                req.getRoomId(),
                req.getCheckInDate(),
                req.getCheckOutDate(),
                numRoom
        );
        if (unavailableDays > 0) {
            throw new RuntimeException("Phòng không đủ số lượng trong khoảng ngày đã chọn");
        }

        long nights = req.getCheckInDate().toEpochDay() - LocalDate.now().toEpochDay();
        long totalNights = req.getCheckOutDate().toEpochDay() - req.getCheckInDate().toEpochDay();
        int pricePerNight = req.getPricePerNight() != null
                ? req.getPricePerNight()
                : (room.getPricePerNight() != null ? room.getPricePerNight() : 0);
        int totalPrice = req.getTotalPrice() != null
                ? req.getTotalPrice()
                : (int) (pricePerNight * numRoom * totalNights);

        Booking booking = Booking.builder()
                .userId(req.getUserId())
                .roomId(req.getRoomId())
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .totalPrice(totalPrice)
                .contactName(req.getContactName())
                .contactPhone(req.getContactPhone())
                .contactEmail(req.getContactEmail())
                .status(1) // PENDING
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        booking = bookingRepository.save(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .amount(totalPrice)
                .status(1) 
                .paymentMethod("VnPay")
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        

        BookingDetail detail = BookingDetail.builder()
                .bookingId(booking.getId())
                .roomId(req.getRoomId())
                .numRoom(numRoom)
                .pricePerNight(pricePerNight)
                .numAdults(req.getNumAdults() != null ? req.getNumAdults() : 1)
                .numChildren(req.getNumChildren() != null ? req.getNumChildren() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bookingDetailRepository.save(detail);

        decreaseAvailability(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate(), numRoom);

        return mapToResponse(booking, List.of(detail));
    }


    public List<BookingResponse> getBookingsByUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(b -> mapToResponse(b, bookingDetailRepository.findByBookingId(b.getId())))
                .collect(Collectors.toList());
    }

    public List<Object[]> getAllBookingsByHotelId(Long hotelId) {
        return bookingRepository.getAllBookingsByHotelId(hotelId);
    }

    public List<Object[]> getBookingByPartnerId(Long userId) {
        return bookingRepository.getBookingByPartnerId(userId);
    }

    public List<Object[]> getAllBookings() {
        return bookingRepository.getAllBookings();
    }


    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: id=" + bookingId));
        List<BookingDetail> details = bookingDetailRepository.findByBookingId(bookingId);
        return mapToResponse(booking, details);
    }

    
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() != 1) {
            throw new IllegalStateException("Chỉ có thể xác nhận booking ở trạng thái PENDING");
        }
        booking.setStatus(2); 
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }



    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() == 3 || booking.getStatus() == 4) {
            throw new IllegalStateException("Không thể hủy booking đang check-in hoặc đã hoàn thành");
        }
        if (booking.getStatus() == 0) {
            throw new IllegalStateException("Booking đã được hủy trước đó");
        }

        List<BookingDetail> details = bookingDetailRepository.findByBookingId(bookingId);

        for (BookingDetail d : details) {
            increaseAvailability(d.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate(), d.getNumRoom());
        }

        booking.setStatus(0); 
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return mapToResponse(booking, details);
    }

    public void payment(boolean isPaid, Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (isPaid) {
            booking.setStatus(2); 
        } else {
            booking.setStatus(0); 
        }
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
    }


    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: id=" + bookingId));
    }

    private void decreaseAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut, int numRoom) {
        List<RoomAvailability> slots = roomAvailabilityRepository
                .findByRoomIdAndDateBetween(roomId, checkIn, checkOut.minusDays(1));

        for (RoomAvailability slot : slots) {
            int updated = slot.getQuantityAvailable() - numRoom;
            if (updated < 0) updated = 0;
            slot.setQuantityAvailable(updated);
        }
        roomAvailabilityRepository.saveAll(slots);
    }

    private void increaseAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut, Integer numRoom) {
        if (numRoom == null || numRoom <= 0) return;

        Room room = roomRepository.findById(roomId).orElse(null);
        int maxQty = room != null && room.getQuantity() != null ? room.getQuantity() : Integer.MAX_VALUE;

        List<RoomAvailability> slots = roomAvailabilityRepository
                .findByRoomIdAndDateBetween(roomId, checkIn, checkOut.minusDays(1));

        for (RoomAvailability slot : slots) {
            int updated = Math.min(slot.getQuantityAvailable() + numRoom, maxQty);
            slot.setQuantityAvailable(updated);
        }
        roomAvailabilityRepository.saveAll(slots);
    }


    private BookingResponse mapToResponse(Booking booking, List<BookingDetail> details) {
        List<BookingDetailResponse> detailResponses = details.stream()
                .map(d -> BookingDetailResponse.builder()
                        .id(d.getId())
                        .roomId(d.getRoomId())
                        .numRoom(d.getNumRoom())
                        .pricePerNight(d.getPricePerNight())
                        .numAdults(d.getNumAdults())
                        .numChildren(d.getNumChildren())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .roomId(booking.getRoomId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .details(detailResponses)
                .build();
    }
}
