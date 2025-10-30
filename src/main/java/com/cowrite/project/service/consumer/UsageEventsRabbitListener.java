package com.cowrite.project.service.consumer;

import com.cowrite.project.service.impl.UsageAggregatorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UsageEventsRabbitListener {
    private static final Logger log = LoggerFactory.getLogger(UsageEventsRabbitListener.class);

    private final UsageAggregatorServiceImpl aggregator;

    public UsageEventsRabbitListener(UsageAggregatorServiceImpl aggregator) {
        this.aggregator = aggregator;
    }

    @RabbitListener(queues = "cowrite.usage.events")
    public void onUsageEvent(@Payload String payload,
                             @Header(name = AmqpHeaders.RECEIVED_ROUTING_KEY, required = false) String routingKey) {
        try {
            String key = routingKey == null ? "" : routingKey;
            if (key.startsWith("document.")) {
                aggregator.aggregateDocumentEditEvent(payload);
            } else if (key.startsWith("ai.")) {
                aggregator.aggregateAiCallEvent(payload);
            } else if (key.startsWith("collaboration.")) {
                aggregator.aggregateCollaborationEvent(payload);
            } else if (key.startsWith("storage.")) {
                aggregator.aggregateStorageUsageEvent(payload);
            } else if (key.startsWith("user.login")) {
                aggregator.aggregateUserLoginEvent(payload);
            } else if (key.startsWith("user.register")) {
                aggregator.aggregateUserRegisterEvent(payload);
            } else {
                // 默认归档到文档编辑类，避免丢失
                aggregator.aggregateDocumentEditEvent(payload);
            }
        } catch (Exception e) {
            log.error("UsageEventsRabbitListener 处理事件失败, routingKey={}, payload={}", routingKey, payload, e);
            throw e;
        }
    }
}


