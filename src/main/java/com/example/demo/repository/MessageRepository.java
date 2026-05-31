package com.example.demo.repository;

import com.example.demo.entity.Message;
import com.example.demo.enums.SenderRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdAndIsDeletedFalseOrderByCreatedAtAsc(Long conversationId);

    Page<Message> findByConversationIdAndIsDeletedFalse(Long conversationId, Pageable pageable);

    long countByConversationIdAndIsReadFalseAndSenderRole(Long conversationId, SenderRole senderRole);
}
