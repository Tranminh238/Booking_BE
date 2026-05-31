package com.example.demo.service;

import com.example.demo.dto.Chat.MessageDTO;
import com.example.demo.dto.Chat.SendMessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final UsersRepository usersRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi tin nhắn: lưu DB + broadcast qua WebSocket + cập nhật conversation
     */
    public MessageDTO sendMessage(SendMessageRequest request) {
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(request.getSenderId())
                .senderRole(request.getSenderRole())
                .content(request.getContent())
                .isRead(false)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message saved = messageRepository.save(message);

        // Cập nhật lastMessage trong conversation
        conversationService.updateLastMessage(
                request.getConversationId(),
                request.getContent(),
                request.getSenderRole()
        );

        MessageDTO dto = toDTO(saved);

        // Broadcast qua WebSocket tới topic của conversation
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(),
                dto
        );

        return dto;
    }

    /**
     * Lấy toàn bộ tin nhắn của 1 conversation (theo thứ tự thời gian)
     */
    public List<MessageDTO> getMessages(Long conversationId) {
        return messageRepository
                .findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Đánh dấu tất cả tin nhắn trong conversation là đã đọc (theo phía nhận)
     */
    public void markMessagesAsRead(Long conversationId) {
        List<Message> messages = messageRepository
                .findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(conversationId);
        messages.forEach(m -> {
            if (!Boolean.TRUE.equals(m.getIsRead())) {
                m.setIsRead(true);
                m.setUpdatedAt(LocalDateTime.now());
            }
        });
        messageRepository.saveAll(messages);
    }

    private MessageDTO toDTO(Message message) {
        String senderName = usersRepository.findByUserId(message.getSenderId())
                .map(u -> {
                    String name = ((u.getFirstName() != null ? u.getFirstName() : "") + " " + (u.getLastName() != null ? u.getLastName() : "")).trim();
                    return name.isEmpty() ? "Người dùng" : name;
                })
                .orElse("Người dùng");

        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .senderName(senderName)
                .senderRole(message.getSenderRole())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
