package com.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.chatbot.models.ChatMessage;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MessagesResponse {
    private List<MessageResponse> data = new ArrayList<>();
    private long totalElements;
    private int totalPages;
    private int currentPage;
    @JsonProperty("isFirst")
    private boolean isFirst;
    @JsonProperty("isLast")
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public MessagesResponse(Page<ChatMessage> result) {
        this.data = result.getContent().stream()
                .map(item -> new MessageResponse(item.getSessionId(), item.getMessage(), item.getRole()))
                .toList();
        this.totalElements = result.getTotalElements();
        this.totalPages = result.getTotalPages();
        this.currentPage = result.getNumber() + 1;
        this.isFirst = result.isFirst();
        this.isLast = result.isLast();
        this.hasNext = result.hasNext();
        this.hasPrevious = result.hasPrevious();
    }

}
