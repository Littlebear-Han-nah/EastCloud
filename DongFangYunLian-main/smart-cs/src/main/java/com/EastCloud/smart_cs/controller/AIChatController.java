package com.EastCloud.smart_cs.controller;

import com.EastCloud.smart_cs.service.QwenAiService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class AIChatController {

    private final QwenAiService qwenAiService;

    public AIChatController(QwenAiService qwenAiService) {
        this.qwenAiService = qwenAiService;
    }

    @PostMapping("/generate-draft")
    public String generateDraft(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return "Error: Message is empty.";
        }
        return qwenAiService.generateDraftWithRAG(message);
    }
}
