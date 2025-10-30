package com.cowrite.project.service;

import com.cowrite.project.model.entity.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cowrite.project.config.RabbitMQConfig.EXCHANGE_EVENTS;

@Service
public class RabbitEventSender {
    private static final Logger log = LoggerFactory.getLogger(RabbitEventSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEvent(OutboxEvent event) {
        String routingKey = buildRoutingKey(event.getEventType());
        rabbitTemplate.convertAndSend(EXCHANGE_EVENTS, routingKey, event.getEventData());
        log.debug("Sent event {} with routingKey {}", event.getEventId(), routingKey);
    }

    private String buildRoutingKey(String eventType) {
        if (eventType == null) {
            return "event.unknown";
        }
        switch (eventType) {
            case "DOCUMENT_EDIT":
                return "document.edit";
            case "DOCUMENT_CREATE":
                return "document.create";
            case "DOCUMENT_DELETE":
                return "document.delete";
            case "AI_CALL":
                return "ai.call";
            case "COLLABORATION":
                return "collaboration.event";
            case "STORAGE_USAGE":
                return "storage.usage";
            case "USER_LOGIN":
                return "user.login";
            case "USER_REGISTER":
                return "user.register";
            default:
                return "event." + eventType.toLowerCase();
        }
    }
}


