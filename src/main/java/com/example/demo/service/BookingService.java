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
import com.example.demo.repository.BookingDetailRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomAvailabilityRepository;
import com.example.demo.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;

    // ─────────────────────────────────────────────
    // Booking Status constants
    // 0 = cancelled | 1 = pending | 2 = confirmed
    // 3 = checked_in | 4 = completed
    // ─────────────────────────────────────────────

    /**
     * Tạo booking mới:
     * 1. Validate ngày & phòng còn trống
     * 2. Tạo Booking (status = PENDING)
     * 3. Tạo BookingDetail
     * 4. Giảm quantityAvailable trong RoomAvailability
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest req) {
        // --- 1. Validate ngày ---
        if (req.getCheckInDate() == null || req.getCheckOutDate() == null) {
            throw new IllegalArgumentException("Ngày check-in và check-out không được để trống");
        }
        if (!req.getCheckInDate().isBefore(req.getCheckOutDate())) {
            throw new IllegalArgumentException("Ngày check-in phải trước ngày check-out");
        }
        if (req.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày check-in không được là ngày trong quá khứ");
        }

        // --- 2. Validate phòng tồn tại ---
        Room room = roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại: id=" + req.getRoomId()));

        if (room.getStatus() == null || room.getStatus() == 0) {
            throw new RuntimeException("Phòng hiện không hoạt động");
        }

        int numRoom = req.getNumRoom() != null ? req.getNumRoom() : 1;

        // --- 3. Kiểm tra availability ---
        long unavailableDays = roomAvailabilityRepository.countUnavailableDays(
                req.getRoomId(),
                req.getCheckInDate(),
                req.getCheckOutDate(),
                numRoom
        );
        if (unavailableDays > 0) {
            throw new RuntimeException("Phòng không đủ số lượng trong khoảng ngày đã chọn");
        }

        // --- 4. Tính tổng tiền nếu chưa có ---
        long nights = req.getCheckInDate().toEpochDay() - LocalDate.now().toEpochDay();
        long totalNights = req.getCheckOutDate().toEpochDay() - req.getCheckInDate().toEpochDay();
        int pricePerNight = req.getPricePerNight() != null
                ? req.getPricePerNight()
                : (room.getPricePerNight() != null ? room.getPricePerNight() : 0);
        int totalPrice = req.getTotalPrice() != null
                ? req.getTotalPrice()
                : (int) (pricePerNight * numRoom * totalNights);

        // --- 5. Lưu Booking ---
        Booking booking = Booking.builder()
                .userId(req.getUserId())
                .roomId(req.getRoomId())
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(1) // PENDING
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        booking = bookingRepository.save(booking);

        // --- 6. Lưu BookingDetail ---
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

        // --- 7. Giảm quantityAvailable trong RoomAvailability ---
        decreaseAvailability(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate(), numRoom);

        return mapToResponse(booking, List.of(detail));
    }


    public List<BookingResponse> getBookingsByUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(b -> mapToResponse(b, bookingDetailRepository.findByBookingId(b.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Lấy chi tiết một booking theo id.
     */
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: id=" + bookingId));
        List<BookingDetail> details = bookingDetailRepository.findByBookingId(bookingId);
        return mapToResponse(booking, details);
    }

    /**
     * Xác nhận booking (PENDING → CONFIRMED).
     * Thường do partner/admin gọi sau khi nhận được thanh toán.
     */
    @Transactional
    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() != 1) {
            throw new IllegalStateException("Chỉ có thể xác nhận booking ở trạng thái PENDING");
        }
        booking.setStatus(2); // CONFIRMED
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }

    /**
     * Check-in: CONFIRMED → CHECKED_IN.
     */
    @Transactional
    public BookingResponse checkIn(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() != 2) {
            throw new IllegalStateException("Chỉ có thể check-in khi booking đã được CONFIRMED");
        }
        booking.setStatus(3); // CHECKED_IN
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }

    /**
     * Check-out / hoàn thành booking: CHECKED_IN → COMPLETED.
     */
    @Transactional
    public BookingResponse checkOut(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() != 3) {
            throw new IllegalStateException("Chỉ có thể check-out khi booking đang ở trạng thái CHECKED_IN");
        }
        booking.setStatus(4); // COMPLETED
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }

    /**
     * Hủy booking: chỉ hủy được khi PENDING hoặc CONFIRMED.
     * Hoàn trả lại quantityAvailable trong RoomAvailability.
     */
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

        // Hoàn trả số lượng phòng
        for (BookingDetail d : details) {
            increaseAvailability(d.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate(), d.getNumRoom());
        }

        booking.setStatus(0); // CANCELLED
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return mapToResponse(booking, details);
    }

    /**
     * Cập nhật trạng thái booking thủ công (admin).
     */
    @Transactional
    public BookingResponse updateStatus(Long bookingId, Integer newStatus) {
        Booking booking = findBookingOrThrow(bookingId);
        booking.setStatus(newStatus);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }

    // ─────────────────────────────────────────────
    // Private helper methods
    // ─────────────────────────────────────────────

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: id=" + bookingId));
    }

    /**
     * Giảm quantityAvailable cho từng ngày trong khoảng [checkIn, checkOut).
     */
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

    /**
     * Tăng quantityAvailable (dùng khi hủy booking).
     */
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

    /**
     * Map Booking + danh sách BookingDetail → BookingResponse.
     */
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
