package com.chatbot.service;


import com.chatbot.common.Constants;
import com.chatbot.dto.MessageRequest;
import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.MessagesResponse;
import com.chatbot.exception.SessionNotFoundException;
import com.chatbot.integration.ChatbotService;
import com.chatbot.models.ChatMessage;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageService.class);
    private final ChatMessageRepository messageRepo;
    private final ChatSessionRepository sessionRepo;
    private final ChatbotService chatClient;

    public ChatMessageService(ChatMessageRepository messageRepo, ChatSessionRepository sessionRepo, ChatbotService chatClient) {
        this.messageRepo = messageRepo;
        this.sessionRepo = sessionRepo;
        this.chatClient = chatClient;
    }

    public MessageResponse sendMessage(MessageRequest request) {

        logger.info("Received message for sessionId={}: {}", request.getSessionId(), request.getUserMessage());

        validateSession(request.getSessionId());

        saveMessage(request.getSessionId(), request.getUserMessage(), Constants.USER_ROLE);

        String botReply = chatClient.ask(request.getUserMessage());

        saveMessage(request.getSessionId(), botReply, Constants.ASSISTANT_ROLE);
        logger.info("Responded to sessionId={} with message: {}", request.getSessionId(), botReply);
        return new MessageResponse(request.getSessionId(), botReply, "assistant");
    }

    public MessagesResponse listMessages(String sessionId, int page, int size) {
        logger.info("Fetching chat messages for sessionId={} with Page={} and size={}", sessionId, page, size);
        validateSession(sessionId);
        int pageNo = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNo, size, Sort.Direction.ASC, "createdAt");
        return new MessagesResponse(messageRepo.findBySessionId(sessionId, pageable));
    }

    private void saveMessage(String sessionId, String message, String role) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setRole(role);
        chatMessage.setMessage(message);
        messageRepo.save(chatMessage);
        logger.debug("Saved message for sessionId={}", sessionId);
    }


    private void validateSession(String sessionId) {
        if (!sessionRepo.existsById(sessionId)) {
            logger.warn("Session not found: {}", sessionId);
            throw new SessionNotFoundException(sessionId);
        }
    }

}

