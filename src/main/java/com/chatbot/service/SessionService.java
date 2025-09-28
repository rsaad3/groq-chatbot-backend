package com.chatbot.service;

import com.chatbot.dto.SessionResponse;
import com.chatbot.exception.SessionNotFoundException;
import com.chatbot.models.ChatSession;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.ChatSessionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public SessionService(ChatSessionRepository sessionRepository, ChatMessageRepository chatMessageRepository) {
        this.sessionRepository = sessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public SessionResponse createSession(String name) {
        ChatSession session = new ChatSession();
        session.setName(name);
        session = sessionRepository.save(session);
        logger.info("Created new chat session with ID: {}", session.getSessionId());
        return new SessionResponse(session.getSessionId(), session.getName(), session.isFavorite());
    }

    public SessionResponse rename(String sessionId, String newName) {
        ChatSession currentSession = findChatSession(sessionId);
        currentSession.setName(newName);
        logger.info("Change name of session: {} to be {}.", sessionId, newName);
        return saveAndGetSessionResponse(currentSession);
    }


    public SessionResponse setFavorite(String sessionId, boolean favorite) {
        ChatSession currentSession = findChatSession(sessionId);
        currentSession.setFavorite(favorite);
        logger.info("Change the favorite flag of session: {} to be {}.", sessionId, favorite);
        return saveAndGetSessionResponse(currentSession);
    }

    @Transactional
    public void deleteSession(String sessionId) {
        findChatSession(sessionId);
        logger.info("Delete the messages of session: {}.", sessionId);
        chatMessageRepository.deleteBySessionId(sessionId);
        logger.info("Delete session: {}.", sessionId);
        sessionRepository.deleteById(sessionId);
    }

    private ChatSession findChatSession(String sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(
                () -> new SessionNotFoundException("Session not found: " + sessionId));
    }

    private SessionResponse saveAndGetSessionResponse(ChatSession currentSession) {
        ChatSession session = sessionRepository.save(currentSession);
        return new SessionResponse(session.getSessionId(), session.getName(), session.isFavorite());
    }
}

