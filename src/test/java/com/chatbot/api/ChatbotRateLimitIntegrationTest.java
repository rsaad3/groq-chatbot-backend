package com.chatbot.api;

import com.chatbot.dto.SessionRequest;
import com.chatbot.dto.SessionResponse;
import com.chatbot.security.RateLimitFilter;
import com.chatbot.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChatbotRateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    private static final String BASE_URL = "/chat/session";
    private static final String MOCK_SESSION_ID = "mock-session-id";
    private static final String MOCK_SESSION_NAME = "mock-session-name";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatSessionController(sessionService))
                .addFilter(rateLimitFilter)
                .build();
    }

    @Test
    void givenTooManyRequestsFromSameIp_whenExceedLimit_thenReturn429() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setName(MOCK_SESSION_NAME);
        SessionResponse mockResponse = new SessionResponse(MOCK_SESSION_ID, MOCK_SESSION_NAME, false);
        given(sessionService.createSession(any(String.class))).willReturn(mockResponse);

        // Default bucket allows 5 requests/min
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // 6st request should fail
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }
}
