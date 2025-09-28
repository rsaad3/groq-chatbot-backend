package com.chatbot.service;

import com.chatbot.dto.MessageRequest;
import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.MessagesResponse;
import com.chatbot.exception.SessionNotFoundException;
import com.chatbot.integration.ChatbotService;
import com.chatbot.models.ChatMessage;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository messageRepo;

    @Mock
    private ChatSessionRepository sessionRepo;

    @Mock
    private ChatbotService chatbotService;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private MessageRequest messageRequest;
    private ChatMessage returnedMessage;
    private static final String MOCK_SESSION_ID = "mock-session-id";
    private static final String MOCK_USER_MESSAGE = "mock-user-message";

    @BeforeEach
    void setUp() {
        messageRequest = new MessageRequest();
        messageRequest.setSessionId(MOCK_SESSION_ID);
        messageRequest.setUserMessage(MOCK_USER_MESSAGE);

        returnedMessage = new ChatMessage();
        returnedMessage.setSessionId(MOCK_SESSION_ID);
        returnedMessage.setMessage("LLM response to: " + MOCK_USER_MESSAGE);
        returnedMessage.setCreatedAt(LocalDateTime.now());
        returnedMessage.setId(1L);
    }


    @Test
    void givenValidSession_whenSendMessage_thenReturnsMessageResponse() {
        given(sessionRepo.existsById(MOCK_SESSION_ID)).willReturn(true);
        given(messageRepo.save(any(ChatMessage.class))).willReturn(returnedMessage);
        given(chatbotService.ask(MOCK_USER_MESSAGE)).willReturn("BOT response to: " + MOCK_USER_MESSAGE);

        MessageResponse response = chatMessageService.sendMessage(messageRequest);

        assertNotNull(response);
        assertEquals("BOT response to: " + MOCK_USER_MESSAGE, response.getMessage());

        verify(messageRepo, times(2)).save(any(ChatMessage.class));
        verify(sessionRepo, times(1)).existsById(MOCK_SESSION_ID);
    }

    @Test
    void givenInvalidSession_whenSendMessage_thenThrowsSessionNotFoundException() {
        given(sessionRepo.existsById(MOCK_SESSION_ID)).willReturn(false);

        assertThrows(SessionNotFoundException.class, () -> chatMessageService.sendMessage(messageRequest));

        verify(messageRepo, never()).save(any(ChatMessage.class));
        verify(chatbotService, never()).ask(anyString());
    }

    @Test
    void givenValidSessionId_whenListMessages_thenReturnsMessages() {
        Page<ChatMessage> page = new PageImpl<>(List.of(returnedMessage));
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "createdAt");
        given(sessionRepo.existsById(MOCK_SESSION_ID)).willReturn(true);
        when(messageRepo.findBySessionId(MOCK_SESSION_ID, pageable)).thenReturn(page);


        MessagesResponse response = chatMessageService.listMessages(MOCK_SESSION_ID, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getData().size());

        verify(messageRepo, times(1)).findBySessionId(anyString(), any(Pageable.class));
    }

    @Test
    void givenInvalidSessionId_whenListMessages_thenThrowsException() {
        when(sessionRepo.existsById(MOCK_SESSION_ID)).thenReturn(false);

        assertThrows(SessionNotFoundException.class, () ->
                chatMessageService.listMessages(MOCK_SESSION_ID, 1, 10));

        verify(messageRepo, never()).findBySessionId(anyString(), any(Pageable.class));
    }
}
