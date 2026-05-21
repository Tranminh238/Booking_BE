package com.example.demo.service;

import com.example.demo.entity.Hotel;
import com.example.demo.entity.HotelAddress;
import com.example.demo.entity.Image;
import com.example.demo.enums.ImageEmun.RefType;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final ChatClient.Builder chatClientBuilder;
    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final ImageRepository imageRepository;
    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;

    // public RecommendResponse recommend(RecommendRequest request) {
    // // 1. Lấy tất cả khách sạn đang hoạt động
    // List<Hotel> activeHotels = hotelRepository.findHotelActive();

    // if (activeHotels.isEmpty()) {
    // return RecommendResponse.builder()
    // .hotels(Collections.emptyList())
    // .aiExplanation("Hiện tại chưa có khách sạn nào đang hoạt động trong hệ
    // thống.")
    // .totalFound(0)
    // .build();
    // }

    // // 2. Lọc theo thành phố (nếu có)
    // List<Hotel> filteredHotels = activeHotels;
    // if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
    // String cityLower = request.getCity().trim().toLowerCase();
    // filteredHotels = activeHotels.stream()
    // .filter(h -> {
    // Optional<HotelAddress> addr =
    // hotelAddressRepository.findByHotelId(h.getId());
    // return addr.map(a -> a.getCity() != null &&
    // a.getCity().toLowerCase().contains(cityLower))
    // .orElse(false);
    // })
    // .collect(Collectors.toList());
    // }

    // // 3. Lọc theo số sao tối thiểu (nếu có)
    // if (request.getMinStar() != null && request.getMinStar() > 0) {
    // filteredHotels = filteredHotels.stream()
    // .filter(h -> h.getStar() != null && h.getStar() >= request.getMinStar())
    // .collect(Collectors.toList());
    // }

    // int totalFound = filteredHotels.size();

    // // 4. Tính điểm cho từng khách sạn
    // List<HotelRecommendItem> scoredHotels = filteredHotels.stream()
    // .map(hotel -> scoreHotel(hotel, request))
    // .filter(Objects::nonNull)
    // .sorted(Comparator.comparingDouble(HotelRecommendItem::getScore).reversed())
    // .limit(5)
    // .collect(Collectors.toList());

    // // 5. Nếu không có kết quả, trả thông báo phù hợp
    // if (scoredHotels.isEmpty()) {
    // return RecommendResponse.builder()
    // .hotels(Collections.emptyList())
    // .aiExplanation("Không tìm thấy khách sạn nào phù hợp với tiêu chí của bạn.
    // Hãy thử mở rộng phạm vi tìm kiếm (giảm số sao yêu cầu, tăng ngân sách, hoặc
    // không chỉ định thành phố).")
    // .totalFound(0)
    // .build();
    // }

    // // 6. Gọi AI tạo lời giải thích
    // String aiExplanation = generateAiExplanation(request, scoredHotels);

    // return RecommendResponse.builder()
    // .hotels(scoredHotels)
    // .aiExplanation(aiExplanation)
    // .totalFound(totalFound)
    // .build();
    // }

    // /**
    // * Tính điểm cho một khách sạn dựa trên tiêu chí người dùng.
    // * Trả về null nếu khách sạn không đáp ứng tiêu chí bắt buộc (ngân sách).
    // */
    // private HotelRecommendItem scoreHotel(Hotel hotel, RecommendRequest request)
    // {
    // double score = 0.0;
    // List<String> reasons = new ArrayList<>();

    // // --- Lấy thông tin khách sạn ---
    // HotelAddress addr =
    // hotelAddressRepository.findByHotelId(hotel.getId()).orElse(null);
    // String city = addr != null ? addr.getCity() : "N/A";
    // String address = addr != null ? (addr.getDistrict() + ", " + addr.getCity())
    // : "N/A";
    // List<String> amenities =
    // hotelAmenitiesRepository.findAmenityNamesByHotelId(hotel.getId());
    // List<String> images = imageRepository.findByRefIdAndRefType(hotel.getId(),
    // RefType.HOTEL)
    // .stream().map(Image::getImageUrl).collect(Collectors.toList());
    // Integer minPrice = roomRepository.findMinPriceByHotelId(hotel.getId());
    // int reviewCount = reviewRepository.totalReview(hotel.getId());

    // // --- Kiểm tra ngân sách (hard filter) ---
    // if (request.getMaxBudget() != null && request.getMaxBudget() > 0) {
    // if (minPrice == null || minPrice > request.getMaxBudget()) {
    // return null; // Loại bỏ khách sạn vượt ngân sách
    // }
    // }

    // // --- Tính điểm RATING (40%) ---
    // float rating = hotel.getRating_avg() != null ? hotel.getRating_avg() : 0f;
    // double ratingScore = (rating / 10.0) * 40.0;
    // score += ratingScore;
    // if (rating >= 8.0f) reasons.add(String.format("đánh giá cao %.1f/10 ⭐",
    // rating));

    // // --- Tính điểm GIÁ PHÙ HỢP (30%) ---
    // if (request.getMaxBudget() != null && request.getMaxBudget() > 0 && minPrice
    // != null) {
    // // Điểm cao hơn nếu giá càng rẻ so với ngân sách (tiết kiệm)
    // double budgetRatio = 1.0 - ((double) minPrice / request.getMaxBudget());
    // double priceScore = Math.max(0, Math.min(budgetRatio, 1.0)) * 30.0;
    // score += priceScore;
    // if (budgetRatio >= 0.3) reasons.add(String.format("giá phòng tiết kiệm từ %,d
    // VNĐ/đêm", minPrice));
    // } else {
    // // Không có yêu cầu ngân sách → cho điểm trung bình
    // score += 15.0;
    // }

    // // --- Tính điểm SỐ SAO (20%) ---
    // int star = hotel.getStar() != null ? hotel.getStar() : 0;
    // double starScore = (star / 5.0) * 20.0;
    // score += starScore;
    // if (star >= 4) reasons.add(star + " sao sang trọng 🏆");

    // // --- Tính điểm TIỆN NGHI (10%) ---
    // if (request.getAmenities() != null && !request.getAmenities().isEmpty()) {
    // long matchedAmenities = request.getAmenities().stream()
    // .filter(reqAmenity -> amenities.stream()
    // .anyMatch(a -> a.toLowerCase().contains(reqAmenity.toLowerCase())))
    // .count();
    // double amenityScore = ((double) matchedAmenities /
    // request.getAmenities().size()) * 10.0;
    // score += amenityScore;
    // if (matchedAmenities > 0) reasons.add(String.format("có %d/%d tiện nghi yêu
    // cầu", matchedAmenities, request.getAmenities().size()));
    // } else {
    // score += 5.0; // Không yêu cầu tiện nghi cụ thể → điểm trung bình
    // }

    // // --- Bonus: Số lượng đánh giá (uy tín) ---
    // if (reviewCount > 10) score += 2.0;
    // if (reviewCount > 50) score += 3.0;

    // String matchReason = reasons.isEmpty()
    // ? "Khách sạn phù hợp với yêu cầu của bạn"
    // : String.join(", ", reasons);

    // return HotelRecommendItem.builder()
    // .id(hotel.getId())
    // .name(hotel.getName())
    // .city(city)
    // .address(address)
    // .star(star)
    // .rating(rating)
    // .reviewCount(reviewCount)
    // .minPrice(minPrice)
    // .amenities(amenities)
    // .images(images)
    // .score(Math.min(score, 100.0))
    // .matchReason(matchReason)
    // .build();
    // }

    // /**
    // * Gọi AI (LLaMA via Groq) để tạo đoạn giải thích thân thiện về kết quả gợi ý.
    // */
    // private String generateAiExplanation(RecommendRequest request,
    // List<HotelRecommendItem> hotels) {
    // try {
    // StringBuilder hotelSummary = new StringBuilder();
    // for (int i = 0; i < hotels.size(); i++) {
    // HotelRecommendItem h = hotels.get(i);
    // hotelSummary.append(String.format("%d. %s (%s) - %d sao, rating: %.1f/10, giá
    // từ %s VNĐ/đêm. Điểm phù hợp: %.0f/100. Lý do: %s\n",
    // i + 1,
    // h.getName(),
    // h.getCity(),
    // h.getStar() != null ? h.getStar() : 0,
    // h.getRating() != null ? h.getRating() : 0f,
    // h.getMinPrice() != null ? String.format("%,d", h.getMinPrice()) : "N/A",
    // h.getScore(),
    // h.getMatchReason()));
    // }

    // String userCriteria = buildCriteriaDescription(request);
    // String prompt = String.format("""
    // Bạn là trợ lý tư vấn khách sạn chuyên nghiệp. Hãy viết một đoạn tư vấn ngắn
    // gọn (3-4 câu) bằng tiếng Việt, lịch sự và thân thiện,
    // giải thích tại sao các khách sạn sau đây là lựa chọn tốt nhất cho khách hàng
    // dựa trên tiêu chí của họ.

    // TIÊU CHÍ CỦA KHÁCH:
    // %s

    // DANH SÁCH KHÁCH SẠN GỢI Ý:
    // %s

    // Hãy đề cập đến khách sạn đứng đầu và nêu lý do tại sao nó phù hợp nhất. Kết
    // thúc bằng lời khuyến nghị nhẹ nhàng.
    // Trả lời ngắn gọn, không quá 100 từ, không dùng bullet points.
    // """, userCriteria, hotelSummary);

    // ChatClient chatClient = chatClientBuilder.build();
    // return chatClient.prompt()
    // .user(prompt)
    // .call()
    // .content();

    // } catch (Exception e) {
    // log.error("Lỗi khi gọi AI explanation: {}", e.getMessage());
    // return buildFallbackExplanation(request, hotels);
    // }
    // }

    // /**
    // * Mô tả tiêu chí người dùng thành văn bản
    // */
    // private String buildCriteriaDescription(RecommendRequest request) {
    // List<String> criteria = new ArrayList<>();
    // if (request.getCity() != null && !request.getCity().trim().isEmpty())
    // criteria.add("Thành phố: " + request.getCity());
    // if (request.getMaxBudget() != null && request.getMaxBudget() > 0)
    // criteria.add(String.format("Ngân sách tối đa: %,d VNĐ/đêm",
    // request.getMaxBudget()));
    // if (request.getMinStar() != null && request.getMinStar() > 0)
    // criteria.add("Số sao tối thiểu: " + request.getMinStar() + " sao");
    // if (request.getNumberOfGuests() != null && request.getNumberOfGuests() > 0)
    // criteria.add("Số khách: " + request.getNumberOfGuests() + " người");
    // if (request.getAmenities() != null && !request.getAmenities().isEmpty())
    // criteria.add("Tiện nghi yêu cầu: " + String.join(", ",
    // request.getAmenities()));
    // return criteria.isEmpty() ? "Không có tiêu chí cụ thể (gợi ý tổng quát)" :
    // String.join("; ", criteria);
    // }

    // /**
    // * Fallback explanation nếu AI không khả dụng
    // */
    // private String buildFallbackExplanation(RecommendRequest request,
    // List<HotelRecommendItem> hotels) {
    // if (hotels.isEmpty()) return "Không tìm thấy khách sạn phù hợp.";
    // HotelRecommendItem top = hotels.get(0);
    // return String.format(
    // "Dựa trên tiêu chí của bạn, chúng tôi tìm thấy %d khách sạn phù hợp. " +
    // "Khách sạn \"%s\" tại %s được gợi ý hàng đầu với điểm phù hợp %.0f/100 vì %s.
    // " +
    // "Hãy xem chi tiết từng khách sạn để chọn lựa tốt nhất! 🏨",
    // hotels.size(), top.getName(), top.getCity(), top.getScore(),
    // top.getMatchReason()
    // );
    // }
}
