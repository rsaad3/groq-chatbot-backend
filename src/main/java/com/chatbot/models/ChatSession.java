package com.chatbot.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
@Setter
@Getter
public class ChatSession {
    @Id
    private String sessionId;
    @NotNull
    private String name;

    private LocalDateTime createdAt;
    private boolean favorite;

    public ChatSession() {
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}

