package com.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SessionRequest {
    @NotBlank
    private String name;
}
