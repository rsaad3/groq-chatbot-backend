package com.chatbot.dto;

import lombok.Getter;

@Getter
public class SessionResponse {
    private String sessionId;
    private String name;
    private boolean favorite;

    public SessionResponse(String sessionId, String name, boolean favorite) {
        this.sessionId = sessionId;
        this.name = name;
        this.favorite = favorite;
    }
}

