package com.chatbot.api;


import com.chatbot.dto.MessageRequest;
import com.chatbot.dto.MessageResponse;
import com.chatbot.dto.MessagesResponse;
import com.chatbot.service.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/session/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatService) {
        this.chatMessageService = chatService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(chatMessageService.sendMessage(request));
    }

    @GetMapping("/{sessionId}")
    public MessagesResponse getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return chatMessageService.listMessages(sessionId, page, size);
    }

//    @GetMapping("/health")
//    public ResponseEntity<String> health() {
//        return ResponseEntity.ok("Service is running");
//    }
}

