package com.chatbot.service;

import com.chatbot.dto.SessionResponse;
import com.chatbot.exception.SessionNotFoundException;
import com.chatbot.models.ChatSession;
import com.chatbot.repository.ChatMessageRepository;
import com.chatbot.repository.ChatSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private ChatSessionRepository sessionRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private SessionService sessionService;

    private static final String MOCK_SESSION_ID = "mock-session-id";
    private static final String MOCK_SESSION_NAME = "mock-session-name";

    private ChatSession mockSession;

    @BeforeEach
    void setUp() {
        mockSession = new ChatSession();
        mockSession.setSessionId(MOCK_SESSION_ID);
        mockSession.setName(MOCK_SESSION_NAME);
    }

    @Test
    void givenSessionName_whenCreateSession_thenReturnsSessionResponse() {
        given(sessionRepository.save(any(ChatSession.class))).willReturn(mockSession);

        SessionResponse response = sessionService.createSession(MOCK_SESSION_NAME);

        assertNotNull(response);
        assertEquals(MOCK_SESSION_ID, response.getSessionId());
        assertEquals(MOCK_SESSION_NAME, response.getName());

        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void givenValidSessionId_whenRename_thenReturnsUpdatedSessionResponse() {
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.of(mockSession));
        given(sessionRepository.save(any(ChatSession.class))).willReturn(mockSession);

        String newName = "New Name";
        SessionResponse response = sessionService.rename(MOCK_SESSION_ID, newName);

        assertNotNull(response);
        assertEquals(MOCK_SESSION_ID, response.getSessionId());
        assertEquals(newName, response.getName());

        verify(sessionRepository, times(1)).findById(MOCK_SESSION_ID);
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void givenInvalidSessionId_whenRename_thenThrowsSessionNotFoundException() {
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () ->
                sessionService.rename(MOCK_SESSION_ID, "New Name"));

        verify(sessionRepository, times(1)).findById(MOCK_SESSION_ID);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void givenValidSessionId_whenSetFavorite_thenReturnsUpdatedSessionResponse() {
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.of(mockSession));
        given(sessionRepository.save(any(ChatSession.class))).willReturn(mockSession);

        SessionResponse response = sessionService.setFavorite(MOCK_SESSION_ID, true);

        assertNotNull(response);
        assertTrue(response.isFavorite());

        verify(sessionRepository, times(1)).findById(MOCK_SESSION_ID);
        verify(sessionRepository, times(1)).save(any(ChatSession.class));
    }

    @Test
    void givenInvalidSessionId_whenSetFavorite_thenThrowsSessionNotFoundException() {
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () ->
                sessionService.setFavorite(MOCK_SESSION_ID, true));

        verify(sessionRepository, times(1)).findById(MOCK_SESSION_ID);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void givenValidSessionId_whenDeleteSession_thenDeletesMessagesAndSession() {
        doNothing().when(chatMessageRepository).deleteBySessionId(MOCK_SESSION_ID);
        doNothing().when(sessionRepository).deleteById(MOCK_SESSION_ID);
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.of(mockSession));

        sessionService.deleteSession(MOCK_SESSION_ID);

        verify(chatMessageRepository, times(1)).deleteBySessionId(MOCK_SESSION_ID);
        verify(sessionRepository, times(1)).deleteById(MOCK_SESSION_ID);
    }

    @Test
    void givenInvalidSessionId_whenDeleteSession_thenThrowsSessionNotFoundException() {
        given(sessionRepository.findById(MOCK_SESSION_ID)).willReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> sessionService.deleteSession(MOCK_SESSION_ID));

        verify(chatMessageRepository, never()).deleteBySessionId(MOCK_SESSION_ID);
        verify(sessionRepository, never()).deleteById(MOCK_SESSION_ID);
    }

}
