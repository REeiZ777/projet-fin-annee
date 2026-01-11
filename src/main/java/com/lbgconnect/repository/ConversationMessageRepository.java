package com.lbgconnect.repository;

import com.lbgconnect.model.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
    List<ConversationMessage> findBySenderIdOrRecipientIdOrderByCreatedAtDesc(Long senderId, Long recipientId);
}
