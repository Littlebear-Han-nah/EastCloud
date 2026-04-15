package com.EastCloud.smart_cs.service;

import com.EastCloud.smart_cs.entity.MessageEntity;
import com.EastCloud.smart_cs.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlingService {

    private final MessageRepository messageRepository;
    private final QwenAiService qwenAiService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageHandlingService(MessageRepository messageRepository, 
                                  QwenAiService qwenAiService, 
                                  SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.qwenAiService = qwenAiService;
        this.messagingTemplate = messagingTemplate;
    }

    public MessageEntity ingestMockMessage(String orderId, String text) {
        MessageEntity entity = new MessageEntity();
        entity.setOrderId(orderId);
        entity.setBuyerMessage(text);
        entity.setStatus("PENDING");
        entity.setIntentTag("ANALYZING");
        MessageEntity saved = messageRepository.save(entity);
        
        // Notify frontend about the new message
        messagingTemplate.convertAndSend("/topic/messages", saved);
        return saved;
    }

    @Async
    public void processMessageAsync(Long messageId) {
        MessageEntity entity = messageRepository.findById(messageId).orElseThrow();
        
        // Use AI to generate draft with RAG context
        String draft = qwenAiService.generateDraftWithRAG(entity.getBuyerMessage());
        
        entity.setAiDraft(draft);
        entity.setStatus("DRAFT_READY");
        
        // Simple heuristic for intent tag for demo
        if (entity.getBuyerMessage().toLowerCase().contains("broken") || entity.getBuyerMessage().toLowerCase().contains("replace")) {
            entity.setIntentTag("Replacement");
        } else if (entity.getBuyerMessage().toLowerCase().contains("refund")) {
            entity.setIntentTag("Refund");
        } else {
            entity.setIntentTag("General Inquiry");
        }
        
        messageRepository.save(entity);
        
        // Push update to frontend
        messagingTemplate.convertAndSend("/topic/messages", entity);
    }
}
