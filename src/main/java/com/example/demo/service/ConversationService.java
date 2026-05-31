package com.example.demo.service;

import com.example.demo.dto.Chat.ConversationDTO;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.User;
import com.example.demo.enums.SenderRole;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final HotelRepository hotelRepository;
    private final UsersRepository usersRepository;

    /**
     * Tạo mới hoặc lấy conversation đã có giữa user và hotel
     */
    public ConversationDTO getOrCreateConversation(Long userId, Long hotelId) {
        Optional<Conversation> existing = conversationRepository.findByUserIdAndHotelId(userId, hotelId);
        if (existing.isPresent()) {
            return toDTO(existing.get());
        }

        Conversation conv = Conversation.builder()
                .userId(userId)
                .hotelId(hotelId)
                .initiatedBy(SenderRole.customer)
                .unreadCountCustomer(0)
                .unreadCountOwner(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toDTO(conversationRepository.save(conv));
    }

    /**
     * Lấy danh sách conversation của khách hàng
     */
    public List<ConversationDTO> getConversationsByUser(Long userId) {
        return conversationRepository.findByUserIdOrderByLastMessageAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách conversation theo hotel
     */
    public List<ConversationDTO> getConversationsByHotel(Long hotelId) {
        return conversationRepository.findByHotelIdOrderByLastMessageAtDesc(hotelId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy conversation theo id
     */
    public Optional<Conversation> findById(Long id) {
        return conversationRepository.findById(id);
    }

    /**
     * Đánh dấu đã đọc cho khách hàng (reset unread của owner)
     */
    public void markReadAsCustomer(Long conversationId) {
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUnreadCountCustomer(0);
            conversationRepository.save(conv);
        });
    }

    /**
     * Đánh dấu đã đọc cho chủ khách sạn (reset unread của customer)
     */
    public void markReadAsOwner(Long conversationId) {
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setUnreadCountOwner(0);
            conversationRepository.save(conv);
        });
    }

    /**
     * Cập nhật lastMessage khi có tin nhắn mới
     */
    public void updateLastMessage(Long conversationId, String content, SenderRole senderRole) {
        conversationRepository.findById(conversationId).ifPresent(conv -> {
            conv.setLastMessage(content);
            conv.setLastMessageAt(LocalDateTime.now());
            conv.setUpdatedAt(LocalDateTime.now());
            // Tăng unread cho phía nhận
            if (senderRole == SenderRole.customer) {
                conv.setUnreadCountOwner(
                        (conv.getUnreadCountOwner() == null ? 0 : conv.getUnreadCountOwner()) + 1
                );
            } else {
                conv.setUnreadCountCustomer(
                        (conv.getUnreadCountCustomer() == null ? 0 : conv.getUnreadCountCustomer()) + 1
                );
            }
            conversationRepository.save(conv);
        });
    }

    private ConversationDTO toDTO(Conversation conv) {
        // Lấy tên user
        String userName = usersRepository.findByUserId(conv.getUserId())
                .map(u -> (u.getFirstName() != null ? u.getFirstName() : "") + " " + (u.getLastName() != null ? u.getLastName() : ""))
                .orElse("Khách hàng")
                .trim();

        // Lấy tên hotel
        String hotelName = hotelRepository.findById(conv.getHotelId())
                .map(Hotel::getName)
                .orElse("Khách sạn");

        return ConversationDTO.builder()
                .id(conv.getId())
                .userId(conv.getUserId())
                .userName(userName.isEmpty() ? "Khách hàng" : userName)
                .hotelId(conv.getHotelId())
                .hotelName(hotelName)
                .initiatedBy(conv.getInitiatedBy())
                .lastMessage(conv.getLastMessage())
                .lastMessageAt(conv.getLastMessageAt())
                .unreadCountCustomer(conv.getUnreadCountCustomer() != null ? conv.getUnreadCountCustomer() : 0)
                .unreadCountOwner(conv.getUnreadCountOwner() != null ? conv.getUnreadCountOwner() : 0)
                .createdAt(conv.getCreatedAt())
                .build();
    }
}
