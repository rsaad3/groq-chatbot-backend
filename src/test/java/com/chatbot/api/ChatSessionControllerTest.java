package com.chatbot.api;

import com.chatbot.dto.SessionRequest;
import com.chatbot.dto.SessionResponse;
import com.chatbot.exception.SessionNotFoundException;
import com.chatbot.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ChatSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/chat/session";
    private static final String MOCK_SESSION_ID = "mock-session-id";
    private static final String MOCK_SESSION_NAME = "mock-session-name";


    @Test
    void givenValidRequest_whenCreateSession_thenReturnSessionResponse() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setName(MOCK_SESSION_NAME);
        SessionResponse mockResponse = new SessionResponse(MOCK_SESSION_ID, MOCK_SESSION_NAME, false);
        given(sessionService.createSession(any(String.class))).willReturn(mockResponse);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId", is(MOCK_SESSION_ID)))
                .andExpect(jsonPath("$.name", is(MOCK_SESSION_NAME)))
                .andExpect(jsonPath("$.favorite", is(false)));
    }

    @Test
    void givenValidSessionIdAndNewName_whenRenameSession_thenReturnUpdatedSession() throws Exception {
        String newName = "Renamed Session";
        SessionRequest request = new SessionRequest();
        request.setName(newName);
        SessionResponse mockResponse = new SessionResponse(MOCK_SESSION_ID, newName, false);
        given(sessionService.rename(any(String.class), any(String.class))).willReturn(mockResponse);

        mockMvc.perform(put(BASE_URL + "/" + MOCK_SESSION_ID + "/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId", is(MOCK_SESSION_ID)))
                .andExpect(jsonPath("$.name", is(newName)));
    }

    @Test
    void givenInvalidSessionId_whenRename_thenReturn404() throws Exception {

        SessionRequest request = new SessionRequest();
        request.setName(MOCK_SESSION_NAME);

        given(sessionService.rename(any(String.class), any(String.class))).willThrow(new SessionNotFoundException("Session not found"));

        mockMvc.perform(put(BASE_URL + "/" + MOCK_SESSION_ID + "/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


    @Test
    void givenValidSessionIdAndFavoriteTrue_whenSetFavorite_thenReturnUpdatedFavoriteStatus() throws Exception {
        SessionResponse mockResponse = new SessionResponse(MOCK_SESSION_ID, MOCK_SESSION_NAME, true);
        given(sessionService.setFavorite(MOCK_SESSION_ID, true)).willReturn(mockResponse);

        mockMvc.perform(put(BASE_URL + "/" + MOCK_SESSION_ID + "/favorite")
                        .param("favorite", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId", is(MOCK_SESSION_ID)))
                .andExpect(jsonPath("$.favorite", is(true)));
    }

    @Test
    void givenInvalidSessionId_whenSetFavorite_thenReturn404() throws Exception {

        given(sessionService.setFavorite(MOCK_SESSION_ID, true)).willThrow(new SessionNotFoundException("Session not found"));

        mockMvc.perform(put(BASE_URL + "/" + MOCK_SESSION_ID + "/favorite")
                        .param("favorite", String.valueOf(true)))
                .andExpect(status().isNotFound());
    }


    @Test
    void givenValidSessionId_whenDeleteSession_thenReturnNoContent() throws Exception {

        doNothing().when(sessionService).deleteSession(MOCK_SESSION_ID);

        mockMvc.perform(delete(BASE_URL + "/" + MOCK_SESSION_ID))
                .andExpect(status().isNoContent());

        verify(sessionService, times(1)).deleteSession(MOCK_SESSION_ID);
    }


    @Test
    void givenInvalidSessionId_whenDeleteSession_thenReturn404() throws Exception {

        doThrow(new SessionNotFoundException("Session not found"))
                .when(sessionService).deleteSession(MOCK_SESSION_ID);

        mockMvc.perform(delete(BASE_URL + "/" + MOCK_SESSION_ID))
                .andExpect(status().isNotFound());

        verify(sessionService, times(1)).deleteSession(MOCK_SESSION_ID);
    }
}