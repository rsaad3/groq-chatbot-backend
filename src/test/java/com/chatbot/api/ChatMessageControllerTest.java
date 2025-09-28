package com.chatbot.api;

import com.chatbot.common.Constants;
import com.chatbot.dto.MessageRequest;
import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.MessagesResponse;
import com.chatbot.models.ChatMessage;
import com.chatbot.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;


@WebMvcTest(ChatMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatMessageService chatMessageService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/chat/session/messages";
    private static final String MOCK_SESSION_ID = "mock-session-id";
    private static final String MOCK_USER_MESSAGE = "Hello chatbot!";
    private static final String MOCK_AI_MESSAGE = "Hi user, how can I help you?";


    @Test
    void givenValidRequest_whenSendMessage_thenReturnMessageResponse() throws Exception {

        MessageRequest request = new MessageRequest();
        request.setSessionId(MOCK_SESSION_ID);
        request.setUserMessage(MOCK_USER_MESSAGE);
        MessageResponse mockResponse = new MessageResponse(MOCK_SESSION_ID, MOCK_AI_MESSAGE, Constants.ASSISTANT_ROLE);

        given(chatMessageService.sendMessage(any(MessageRequest.class))).willReturn(mockResponse);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId", is(MOCK_SESSION_ID)))
                .andExpect(jsonPath("$.role", is(Constants.ASSISTANT_ROLE)))
                .andExpect(jsonPath("$.message", is(MOCK_AI_MESSAGE)));

        verify(chatMessageService, times(1)).sendMessage(any(MessageRequest.class));
    }


    @Test
    void givenValidSessionId_whenGetMessages_thenReturnMessagesResponse() throws Exception {

        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(MOCK_SESSION_ID);
        userMessage.setMessage(MOCK_USER_MESSAGE);
        userMessage.setRole("user");

        ChatMessage aiMessage = new ChatMessage();
        aiMessage.setSessionId(MOCK_SESSION_ID);
        aiMessage.setMessage(MOCK_AI_MESSAGE);
        aiMessage.setRole("assistant");

        Page<ChatMessage> page = new PageImpl<>(List.of(userMessage, aiMessage), PageRequest.of(0, 10), 2);
        MessagesResponse mockResponse = new MessagesResponse(page);


        given(chatMessageService.listMessages(eq(MOCK_SESSION_ID), anyInt(), anyInt()))
                .willReturn(mockResponse);

        mockMvc.perform(get(BASE_URL + "/" + MOCK_SESSION_ID)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)))
                .andExpect(jsonPath("$.isLast", is(true)));

        verify(chatMessageService, times(1)).listMessages(eq(MOCK_SESSION_ID), anyInt(), anyInt());

    }

}