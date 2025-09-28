package com.chatbot.repository;

import com.chatbot.models.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    Page<ChatMessage> findBySessionId(String sessionId, Pageable pageable);

    void deleteBySessionId(String sessionId);
}
