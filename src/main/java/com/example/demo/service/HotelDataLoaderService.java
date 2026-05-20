package com.example.demo.service;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelDataLoaderService {

    private final ChatClient.Builder chatClientBuilder;
    private final HotelRepository hotelRepository;
    private final HotelAddressRepository hotelAddressRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final HotelPolicyRepository hotelPolicyRepository;

    // Lưu lịch sử hội thoại theo sessionId (tối đa 20 tin nhắn)
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();
    private static final int MAX_HISTORY = 20;

    /**
     * Lấy toàn bộ dữ liệu khách sạn từ DB và xây dựng context cho AI
     */
    private String buildHotelContext() {
        StringBuilder context = new StringBuilder();
        context.append("=== DỮ LIỆU KHÁCH SẠN TRONG HỆ THỐNG ===\n\n");

        // Lấy danh sách khách sạn đang hoạt động (status = 2)
        List<Hotel> activeHotels = hotelRepository.findHotelActive();
        if (activeHotels.isEmpty()) {
            context.append("Hiện tại chưa có khách sạn nào đang hoạt động.\n");
            return context.toString();
        }

        context.append(String.format("Tổng số khách sạn đang hoạt động: %d\n\n", activeHotels.size()));

        for (Hotel hotel : activeHotels) {
            context.append("---\n");
            context.append(String.format("🏨 KHÁCH SẠN: %s (ID: %d)\n", hotel.getName(), hotel.getId()));
            context.append(String.format("   Số sao: %d ⭐\n", hotel.getStar() != null ? hotel.getStar() : 0));
            context.append(String.format("   Điểm đánh giá trung bình: %.1f/10\n",
                    hotel.getRating_avg() != null ? hotel.getRating_avg() : 0.0));

            // Giờ check-in / check-out
            if (hotel.getCheckin_time_start() != null) {
                context.append(String.format("   Giờ nhận phòng: %s - %s\n",
                        hotel.getCheckin_time_start(), hotel.getCheckin_time_end()));
            }
            if (hotel.getCheckout_time_start() != null) {
                context.append(String.format("   Giờ trả phòng: %s - %s\n",
                        hotel.getCheckout_time_start(), hotel.getCheckout_time_end()));
            }

            // Mô tả khách sạn
            if (hotel.getDescription() != null && !hotel.getDescription().isEmpty()) {
                String desc = hotel.getDescription().length() > 300
                        ? hotel.getDescription().substring(0, 300) + "..."
                        : hotel.getDescription();
                context.append(String.format("   Mô tả: %s\n", desc));
            }

            // Địa chỉ
            hotelAddressRepository.findByHotelId(hotel.getId()).ifPresent(addr ->
                context.append(String.format("   📍 Địa chỉ: %s, %s, %s\n",
                        addr.getDistrict(), addr.getCity(), addr.getCountry()))
            );

            // Chính sách khách sạn
            hotelPolicyRepository.findByHotelId(hotel.getId()).ifPresent(policy -> {
                context.append("   📋 Chính sách:\n");
                if (policy.getIdentificationDocuments() != null)
                    context.append(String.format("      - Giấy tờ cần thiết: %s\n", policy.getIdentificationDocuments()));
                if (policy.getCheckInInstructions() != null)
                    context.append(String.format("      - Hướng dẫn nhận phòng: %s\n", policy.getCheckInInstructions()));
                if (policy.getSmokePolicy() != null)
                    context.append(String.format("      - Chính sách hút thuốc: %s\n", policy.getSmokePolicy()));
                if (policy.getPetPolicy() != null)
                    context.append(String.format("      - Chính sách thú cưng: %s\n", policy.getPetPolicy()));
            });

            // Tiện nghi khách sạn (dùng đúng method có sẵn trong repository)
            List<String> amenityNames = hotelAmenitiesRepository.findAmenityNamesByHotelId(hotel.getId());
            if (!amenityNames.isEmpty()) {
                context.append(String.format("   🛎️ Tiện nghi: %s\n", String.join(", ", amenityNames)));
            }

            // Phòng của khách sạn
            List<Room> rooms = roomRepository.findByHotelId(hotel.getId());
            List<Room> availableRooms = rooms.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() == 1)
                    .collect(Collectors.toList());

            if (!availableRooms.isEmpty()) {
                context.append(String.format("   🛏️ Phòng có sẵn (%d loại phòng):\n", availableRooms.size()));
                for (Room room : availableRooms) {
                    String roomTypeName = roomTypeRepository.findById(room.getRoomTypeId())
                            .map(RoomType::getName)
                            .orElse("Không xác định");
                    context.append(String.format("      • Loại phòng: %s | ", roomTypeName));
                    context.append(String.format("Giá: %,d VNĐ/đêm | ", room.getPricePerNight() != null ? room.getPricePerNight() : 0));
                    context.append(String.format("Sức chứa: %d người | ", room.getCapacity() != null ? room.getCapacity() : 0));
                    context.append(String.format("Diện tích: %d m² | ", room.getArea() != null ? room.getArea() : 0));
                    context.append(String.format("Số lượng: %d phòng\n", room.getQuantity() != null ? room.getQuantity() : 0));
                    if (room.getDescription() != null && !room.getDescription().isEmpty()) {
                        String roomDesc = room.getDescription().length() > 150
                                ? room.getDescription().substring(0, 150) + "..."
                                : room.getDescription();
                        context.append(String.format("        Mô tả: %s\n", roomDesc));
                    }
                }
            } else {
                context.append("   🛏️ Hiện không có phòng nào khả dụng\n");
            }

            context.append("\n");
        }

        return context.toString();
    }

    /**
     * Xây dựng system prompt cho chatbot
     */
    private String buildSystemPrompt() {
        String hotelContext = buildHotelContext();
        return """
                Bạn là trợ lý AI thân thiện của hệ thống đặt phòng khách sạn.
                Nhiệm vụ của bạn là tư vấn và cung cấp thông tin về các khách sạn, phòng, tiện nghi, chính sách cho khách hàng.
                
                NGUYÊN TẮC TRẢ LỜI:
                1. Chỉ trả lời các câu hỏi liên quan đến khách sạn, phòng, đặt phòng, du lịch.
                2. Luôn trả lời bằng tiếng Việt, lịch sự và chuyên nghiệp.
                3. Nếu khách hỏi thông tin không có trong dữ liệu, hãy nói rõ bạn không có thông tin đó.
                4. Sử dụng emoji phù hợp để câu trả lời sinh động hơn.
                5. Khi so sánh hoặc gợi ý, hãy dựa vào dữ liệu thực tế bên dưới.
                6. Định dạng giá tiền theo kiểu: 500,000 VNĐ (có dấu phẩy phân cách hàng nghìn).
                7. Nếu câu hỏi không liên quan đến khách sạn/du lịch, hãy lịch sự từ chối và hướng dẫn khách hỏi đúng chủ đề.
                8. Khi liệt kê nhiều mục, hãy dùng định dạng gọn gàng với dấu gạch đầu dòng.
                
                """ + hotelContext;
    }

    /**
     * Xử lý tin nhắn chat với lịch sử hội thoại
     */
    public ChatResponse chat(ChatRequest request) {
        String sessionId = (request.sessionId() != null && !request.sessionId().isEmpty())
                ? request.sessionId()
                : UUID.randomUUID().toString();

        // Lấy hoặc khởi tạo lịch sử hội thoại
        List<Message> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        // Thêm tin nhắn của user vào lịch sử
        history.add(new UserMessage(request.message()));

        // Giới hạn lịch sử tối đa MAX_HISTORY tin nhắn
        if (history.size() > MAX_HISTORY) {
            history = new ArrayList<>(history.subList(history.size() - MAX_HISTORY, history.size()));
            conversationHistory.put(sessionId, history);
        }

        try {
            ChatClient chatClient = chatClientBuilder.build();
            String systemPrompt = buildSystemPrompt();

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .messages(history)
                    .call()
                    .content();

            // Lưu phản hồi của AI vào lịch sử
            history.add(new AssistantMessage(response));

            return new ChatResponse(response, sessionId);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API: {}", e.getMessage(), e);
            String errorMsg = "Xin lỗi, tôi đang gặp sự cố kết nối. Vui lòng thử lại sau ít phút! 🙏";
            return new ChatResponse(errorMsg, sessionId);
        }
    }

    /**
     * Xóa lịch sử hội thoại của một session
     */
    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
    }
}