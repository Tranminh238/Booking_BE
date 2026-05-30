package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.Booking.Request.BookingRequest;
import com.example.demo.dto.Booking.Response.BookingResponse;
import com.example.demo.dto.Booking.Response.BookingDetailDTO;
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
import com.example.demo.entity.Image;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.HotelAddress;
import com.example.demo.entity.Promotion;
import com.example.demo.entity.User;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.HotelAddressRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.repository.PromotionRepository;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final PaymentRepository paymentRepository;
    private final PromotionService promotionService;
    private final ImageRepository imageRepository;
    private final EmailService emailService;
    private final UsersRepository usersRepository;
    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final PromotionRepository promotionRepository;

    @Transactional
    //status: 1=WAITING, 2=CONFIRMED, 3=COMPLETE, 0=CANCELLED
    public BookingResponse createBooking(BookingRequest req) {
        if (req.getUserId() == null) {
            throw new IllegalArgumentException("Người dùng không được để trống");
        }
        if (req.getRoomId() == null) {
            throw new IllegalArgumentException("Phòng không được để trống");
        }
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

        
        int basePrice = room.getPricePerNight() != null ? room.getPricePerNight() : 0;
        int totalPrice = (int) Math.round(calculateTotalPrice(
            req.getRoomId(),
            req.getCheckInDate(),
            req.getCheckOutDate(),
            basePrice,
            numRoom
        ));
        int displayPricePerNight = (int) Math.round(getPriceForDate(req.getRoomId(), req.getCheckInDate(), basePrice));



        Booking booking = Booking.builder()
                .userId(req.getUserId())
                .roomId(req.getRoomId())
                .checkInDate(req.getCheckInDate())
                .checkOutDate(req.getCheckOutDate())
                .totalPrice(totalPrice)
                .contactName(req.getContactName())
                .contactPhone(req.getContactPhone())
                .contactEmail(req.getContactEmail())
                .message(req.getMessage())
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
                .pricePerNight(displayPricePerNight)
                .numAdults(req.getNumAdults() != null ? req.getNumAdults() : 1)
                .numChildren(req.getNumChildren() != null ? req.getNumChildren() : 0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        bookingDetailRepository.save(detail);

        decreaseAvailability(req.getRoomId(), req.getCheckInDate(), req.getCheckOutDate(), numRoom);

        return mapToResponse(booking, List.of(detail));
    }
    private double calculateTotalPrice(Long roomId, LocalDate checkIn, LocalDate checkOut,
                                 int basePrice, int numRoom) {
        double total = 0;
        LocalDate current = checkIn;
        while (current.isBefore(checkOut)) {
            double priceThisNight = getPriceForDate(roomId, current, basePrice);
            total += priceThisNight * numRoom;
            current = current.plusDays(1);
        }

        return total;
    }

    private double getPriceForDate(Long roomId, LocalDate date, int basePrice) {
        return promotionRepository.findActivePromotionsForRoomAndDate(roomId, date)
                .stream()
                .filter(p -> p.getQuantityUsed() < p.getQuantityRoom())
                .max(Comparator.comparingInt(Promotion::getDiscountPercentage))
                .map(p -> basePrice * (100 - p.getDiscountPercentage()) / 100.0)
                .orElse((double) basePrice);
    }


    public List<BookingDetailDTO> getBookingsByUser(Long userId) {
        List<BookingDetailDTO> bookings = bookingRepository.findByUserId(userId);
        bookings.forEach(b -> {
            if (b.getHotelId() != null) {
                List<String> urls = imageRepository
                        .findByRefIdAndRefType(b.getHotelId(), RefType.HOTEL)
                        .stream()
                        .map(Image::getImageUrl)
                        .collect(Collectors.toList());
                b.setImageUrl(urls);
            }
        });
        return bookings;
    }

    public List<BookingDetailDTO> getAllBookingsByHotelId(Long hotelId) {
        return bookingRepository.getAllBookingsByHotelId(hotelId);
    }

    public List<BookingDetailDTO> getBookingByPartnerId(Long userId) {
        return bookingRepository.getBookingByPartnerId(userId);
    }

    public List<BookingDetailDTO> getAllBookings() {
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
    public BookingResponse completeBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() != 2) {
            throw new IllegalStateException("Chỉ có thể hoàn thành booking ở trạng thái CONFIRMED");
        }
        if(booking.getCheckOutDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Không thể hoàn thành booking sau ngày check-out");
        }
        booking.setStatus(3); 
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);
        return mapToResponse(booking, bookingDetailRepository.findByBookingId(bookingId));
    }



    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (booking.getStatus() == 3 ) {
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
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            Payment payment = paymentRepository.findByBookingId(bookingId).orElseThrow();
            payment.setStatus(2);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            try {
                Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
                Hotel hotel = (room != null)
                    ? hotelRepository.findById(room.getHotelId()).orElse(null)
                    : null;
                HotelAddress hotelAddr = (hotel != null)
                    ? hotelAddressRepository.findByHotelId(hotel.getId()).orElse(null)
                    : null;

                String hotelName = hotel != null ? hotel.getName() : "Khách sạn";
                String hotelCity = hotelAddr != null ? hotelAddr.getCity() : "";

                String toEmail = booking.getContactEmail();
                String customerName = booking.getContactName();

                if ((toEmail == null || toEmail.isBlank()) && booking.getUserId() != null) {
                    User user = usersRepository.findByUserId(booking.getUserId()).orElse(null);
                    if (user != null) {
                        toEmail = user.getEmail();
                        customerName = user.getFirstName() + " " + user.getLastName();
                    }
                }

                if (toEmail != null && !toEmail.isBlank()) {
                    emailService.sendBookingConfirmationEmail(
                        toEmail,
                        customerName != null ? customerName : "Quý khách",
                        booking.getId(),
                        hotelName,
                        hotelCity,
                        booking.getCheckInDate().toString(),
                        booking.getCheckOutDate().toString(),
                        booking.getTotalPrice() != null ? booking.getTotalPrice() : 0
                    );
                }
            } catch (Exception e) {
                System.err.println("[BookingService] Lỗi gửi email xác nhận: " + e.getMessage());
            }
        } else {
            booking.setStatus(1);
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            // Cập nhật payment thành thất bại
            paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
                p.setStatus(1);
                p.setUpdatedAt(LocalDateTime.now());
                paymentRepository.save(p);
            });
        }
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
                .contactName(booking.getContactName())
                .contactPhone(booking.getContactPhone())
                .contactEmail(booking.getContactEmail())
                .message(booking.getMessage())
                .details(detailResponses)
                .build();
    }
}
