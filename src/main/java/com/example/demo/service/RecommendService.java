package com.example.demo.service;

import com.example.demo.dto.Hotel.response.HotelResponse;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.SearchHistory;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final ImageRepository imageRepository;
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final HotelPolicyRepository hotelPolicyRepository;
    private final BookingRepository bookingRepository;
    private final SearchHotelRepository searchHotelRepository;
    private final WishlistRepository wishlistRepository;
    private final HotelService hotelService;

    /**
     * Gợi ý khách sạn dựa trên lịch sử đặt phòng, tìm kiếm và wishlist
     * Ưu tiên: wishlist > booking history > search history > popular
     */
    public List<HotelResponse> getRecommendations(Long userId, int limit) {
        // LinkedHashMap để giữ thứ tự và tránh trùng lặp
        Map<Long, Hotel> recommendedMap = new LinkedHashMap<>();

        // 1. Từ Wishlist - ưu tiên cao nhất
        try {
            List<Long> wishlistIds = wishlistRepository.findByUserId(userId)
                    .stream().map(w -> w.getHotelId()).collect(Collectors.toList());
            if (!wishlistIds.isEmpty()) {
                List<Hotel> wishlistHotels = hotelRepository.findActiveByIds(wishlistIds);
                for (Hotel h : wishlistHotels) {
                    if (recommendedMap.size() >= limit * 3) break;
                    recommendedMap.put(h.getId(), h);
                }
            }
        } catch (Exception e) {
            log.warn("Lỗi khi lấy wishlist: {}", e.getMessage());
        }

        // 2. Từ Lịch sử đặt phòng - lấy các khách sạn cùng thành phố
        try {
            List<Long> bookedHotelIds = bookingRepository.findDistinctHotelIdsByUserId(userId);
            if (!bookedHotelIds.isEmpty()) {
                // Lấy thành phố của các khách sạn đã đặt
                List<String> bookedCities = new ArrayList<>();
                for (Long hotelId : bookedHotelIds) {
                    hotelAddressRepository.findByHotelId(hotelId).ifPresent(addr -> {
                        if (addr.getCity() != null && !addr.getCity().isEmpty()) {
                            bookedCities.add(addr.getCity());
                        }
                    });
                }
                // Tìm khách sạn cùng thành phố chưa được thêm
                for (String city : bookedCities) {
                    if (recommendedMap.size() >= limit * 3) break;
                    List<Hotel> cityHotels = hotelRepository.findActiveHotelsByKeyword(city);
                    for (Hotel h : cityHotels) {
                        if (!bookedHotelIds.contains(h.getId())) { // Không gợi ý lại ks đã đặt
                            recommendedMap.put(h.getId(), h);
                        }
                    }
                }
                // Cũng thêm các khách sạn đã đặt (nếu chưa có)
                List<Hotel> bookedHotels = hotelRepository.findActiveByIds(bookedHotelIds);
                for (Hotel h : bookedHotels) {
                    if (recommendedMap.size() >= limit * 3) break;
                    recommendedMap.putIfAbsent(h.getId(), h);
                }
            }
        } catch (Exception e) {
            log.warn("Lỗi khi lấy booking history: {}", e.getMessage());
        }

        // 3. Từ Lịch sử tìm kiếm
        try {
            List<SearchHistory> searchHistories = searchHotelRepository.findRecentByUserId(userId);
            Set<String> usedKeywords = new LinkedHashSet<>();
            for (SearchHistory sh : searchHistories) {
                if (sh.getKeyword() != null && !sh.getKeyword().isEmpty()) {
                    usedKeywords.add(sh.getKeyword().toLowerCase().trim());
                }
                if (usedKeywords.size() >= 5) break; // Chỉ lấy 5 keyword gần nhất
            }
            for (String keyword : usedKeywords) {
                if (recommendedMap.size() >= limit * 3) break;
                List<Hotel> keywordHotels = hotelRepository.findActiveHotelsByKeyword(keyword);
                for (Hotel h : keywordHotels) {
                    recommendedMap.putIfAbsent(h.getId(), h);
                }
            }
        } catch (Exception e) {
            log.warn("Lỗi khi lấy search history: {}", e.getMessage());
        }

        // 4. Fallback: Nếu chưa đủ, lấy khách sạn popular (rating cao)
        if (recommendedMap.size() < limit) {
            try {
                List<Hotel> allActive = hotelRepository.findHotelActive();
                allActive.sort((a, b) -> {
                    float ra = a.getRating_avg() != null ? a.getRating_avg() : 0f;
                    float rb = b.getRating_avg() != null ? b.getRating_avg() : 0f;
                    return Float.compare(rb, ra);
                });
                for (Hotel h : allActive) {
                    if (recommendedMap.size() >= limit) break;
                    recommendedMap.putIfAbsent(h.getId(), h);
                }
            } catch (Exception e) {
                log.warn("Lỗi khi lấy popular hotels: {}", e.getMessage());
            }
        }

        // Chuyển đổi sang HotelResponse và giới hạn số lượng
        return recommendedMap.values().stream()
                .limit(limit)
                .map(hotelService::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý khi chưa đăng nhập - trả về khách sạn phổ biến nhất
     */
    // public List<HotelResponse> getPopularHotels(int limit) {
    //     List<Hotel> allActive = hotelRepository.findHotelActive();
    //     allActive.sort((a, b) -> {
    //         float ra = a.getRating_avg() != null ? a.getRating_avg() : 0f;
    //         float rb = b.getRating_avg() != null ? b.getRating_avg() : 0f;
    //         return Float.compare(rb, ra);
    //     });
    //     return allActive.stream()
    //             .limit(limit)
    //             .map(hotelService::toResponse)
    //             .collect(Collectors.toList());
    // }
}
