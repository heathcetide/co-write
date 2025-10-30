package com.cowrite.project.scheduler;

import com.cowrite.project.model.entity.OutboxEvent;
import com.cowrite.project.service.OutboxEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Outbox事件处理器 - 定时发送待处理的事件
 *
 * @author heathcetide
 */
@Component
public class OutboxEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventProcessor.class);

    @Autowired
    private OutboxEventService outboxEventService;

    @Autowired
    private com.cowrite.project.service.RabbitEventSender rabbitEventSender;

    /**
     * 每5秒处理一次待发送的事件
     */
    @Scheduled(fixedDelay = 5000)
    public void processPendingEvents() {
        try {
            List<OutboxEvent> pendingEvents = outboxEventService.getPendingEvents(10);
            if (!pendingEvents.isEmpty()) {
                logger.info("处理 {} 个待发送事件", pendingEvents.size());
                
                for (OutboxEvent event : pendingEvents) {
                    try {
                        rabbitEventSender.sendEvent(event);
                        outboxEventService.markAsSent(event.getEventId());
                    } catch (Exception e) {
                        logger.error("发送事件失败: {}", event.getEventId(), e);
                        outboxEventService.markAsFailed(event.getEventId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理待发送事件时发生错误", e);
        }
    }

    /**
     * 每30秒处理一次重试事件
     */
    @Scheduled(fixedDelay = 30000)
    public void processRetryEvents() {
        try {
            List<OutboxEvent> retryEvents = outboxEventService.getRetryEvents(5);
            if (!retryEvents.isEmpty()) {
                logger.info("处理 {} 个重试事件", retryEvents.size());
                
                for (OutboxEvent event : retryEvents) {
                    try {
                        rabbitEventSender.sendEvent(event);
                        outboxEventService.markAsSent(event.getEventId());
                    } catch (Exception e) {
                        logger.error("重试发送事件失败: {}", event.getEventId(), e);
                        outboxEventService.incrementRetryCount(event.getEventId());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("处理重试事件时发生错误", e);
        }
    }
}
