package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat/{userId}")
    public ResponseEntity<AiChatResponse> chat(@PathVariable String userId, @RequestBody AiChatRequest request) {
        String aiResponseText = aiChatService.getChatResponse(userId, request.getMessage());
        return ResponseEntity.ok(new AiChatResponse(aiResponseText));
    }
}
