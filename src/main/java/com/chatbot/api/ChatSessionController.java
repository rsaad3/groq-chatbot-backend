package com.chatbot.api;

import com.chatbot.dto.SessionRequest;
import com.chatbot.dto.SessionResponse;
import com.chatbot.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/session")
public class ChatSessionController {

    private final SessionService sessionService;

    public ChatSessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionRequest req) {
        return ResponseEntity.ok(sessionService.createSession(req.getName()));
    }

    @PutMapping("/{id}/rename")
    public ResponseEntity<SessionResponse> rename(@PathVariable String id, @RequestBody SessionRequest req) {
        return ResponseEntity.ok(sessionService.rename(id, req.getName()));
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<SessionResponse> setFavorite(@PathVariable String id, @RequestParam boolean favorite) {
        return ResponseEntity.ok(sessionService.setFavorite(id, favorite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

}
