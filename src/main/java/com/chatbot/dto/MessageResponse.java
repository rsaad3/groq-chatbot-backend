package com.chatbot.dto;

import lombok.Data;

@Data
public class MessageResponse {
    private String sessionId;
    private String message;
    private String role;

    public MessageResponse(String sessionId, String message, String role) {
        this.sessionId = sessionId;
        this.message = message;
        this.role = role;
    }
}
