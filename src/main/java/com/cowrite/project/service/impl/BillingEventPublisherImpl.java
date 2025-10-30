package com.cowrite.project.service.impl;

import com.cowrite.project.model.entity.OutboxEvent;
import com.cowrite.project.service.BillingEventPublisher;
import com.cowrite.project.service.OutboxEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 计费事件发布器实现
 *
 * @author heathcetide
 */
@Service
public class BillingEventPublisherImpl implements BillingEventPublisher {

    @Autowired
    private OutboxEventService outboxEventService;

    @Override
    public void publishDocumentEditEvent(Long userId, String documentId, String operation, Object metadata) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("documentId", documentId);
        eventData.put("operation", operation);
        eventData.put("metadata", metadata);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.DOCUMENT_EDIT.getCode(),
            documentId,
            eventData
        );
    }

    @Override
    public void publishAiCallEvent(Long userId, String documentId, Integer tokens, String model) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("documentId", documentId);
        eventData.put("tokens", tokens);
        eventData.put("model", model);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.AI_CALL.getCode(),
            documentId,
            eventData
        );
    }

    @Override
    public void publishCollaborationEvent(Long userId, String documentId, String operation, Object metadata) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("documentId", documentId);
        eventData.put("operation", operation);
        eventData.put("metadata", metadata);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.COLLABORATION.getCode(),
            documentId,
            eventData
        );
    }

    @Override
    public void publishStorageUsageEvent(Long userId, String documentId, Long bytes, String operation) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("documentId", documentId);
        eventData.put("bytes", bytes);
        eventData.put("operation", operation);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.STORAGE_USAGE.getCode(),
            documentId,
            eventData
        );
    }

    @Override
    public void publishUserLoginEvent(Long userId, String ip) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("ip", ip);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.USER_LOGIN.getCode(),
            userId.toString(),
            eventData
        );
    }

    @Override
    public void publishUserRegisterEvent(Long userId, String source) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("source", source);
        eventData.put("timestamp", LocalDateTime.now());
        
        outboxEventService.saveEvent(
            OutboxEvent.EventType.USER_REGISTER.getCode(),
            userId.toString(),
            eventData
        );
    }
}
