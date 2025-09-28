package com.chatbot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String sessionId;
    @NotNull
    private String role;
    @Column(columnDefinition = "TEXT")
    @NotNull
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

}

