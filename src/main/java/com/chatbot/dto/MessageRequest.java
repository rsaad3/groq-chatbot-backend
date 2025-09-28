package com.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {
    @NotBlank
    private String sessionId;

    @NotBlank
    private String userMessage;

}

