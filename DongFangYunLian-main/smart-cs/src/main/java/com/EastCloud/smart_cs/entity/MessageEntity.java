package com.EastCloud.smart_cs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "order_123"
    private String orderId;

    // Incoming buyer message
    @Column(columnDefinition = "TEXT")
    private String buyerMessage;

    // AI Generated Response Draft
    @Column(columnDefinition = "TEXT")
    private String aiDraft;

    // AI Intent tag (e.g. "Complaint", "Return")
    private String intentTag;

    // State: "PENDING", "DRAFT_READY", "SENT"
    private String status;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getBuyerMessage() { return buyerMessage; }
    public void setBuyerMessage(String buyerMessage) { this.buyerMessage = buyerMessage; }
    public String getAiDraft() { return aiDraft; }
    public void setAiDraft(String aiDraft) { this.aiDraft = aiDraft; }
    public String getIntentTag() { return intentTag; }
    public void setIntentTag(String intentTag) { this.intentTag = intentTag; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
