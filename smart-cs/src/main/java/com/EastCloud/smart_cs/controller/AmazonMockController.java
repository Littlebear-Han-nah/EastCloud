package com.EastCloud.smart_cs.controller;

import com.EastCloud.smart_cs.entity.MessageEntity;
import com.EastCloud.smart_cs.repository.MessageRepository;
import com.EastCloud.smart_cs.service.MessageHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AmazonMockController {

    @Autowired
    private MessageHandlingService messageHandlingService;
    
    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/mock/messages")
    public MessageEntity receiveMockMessage(@RequestBody Map<String, String> payload) {
        String orderId = payload.getOrDefault("orderId", "114-1234567");
        String text = payload.get("text");
        
        MessageEntity saved = messageHandlingService.ingestMockMessage(orderId, text);
        messageHandlingService.processMessageAsync(saved.getId());
        
        return saved;
    }
    
    @GetMapping("/messages")
    public List<MessageEntity> getMessages() {
        return messageRepository.findAll();
    }
}