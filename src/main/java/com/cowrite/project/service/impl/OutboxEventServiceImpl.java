package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.OutboxEventMapper;
import com.cowrite.project.model.entity.OutboxEvent;
import com.cowrite.project.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Outbox事件服务实现
 *
 * @author heathcetide
 */
@Service
public class OutboxEventServiceImpl extends ServiceImpl<OutboxEventMapper, OutboxEvent> implements OutboxEventService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public String saveEvent(String eventType, String aggregateId, Object eventData) {
        String eventId = UUID.randomUUID().toString();
        
        OutboxEvent event = new OutboxEvent();
        event.setEventId(eventId);
        event.setEventType(eventType);
        event.setAggregateId(aggregateId);
        event.setStatus(OutboxEvent.EventStatus.PENDING.getCode());
        event.setRetryCount(0);
        event.setMaxRetries(3);
        event.setNextRetryAt(LocalDateTime.now());
        
        try {
            event.setEventData(objectMapper.writeValueAsString(eventData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化事件数据失败", e);
        }
        
        save(event);
        return eventId;
    }

    @Override
    public List<OutboxEvent> getPendingEvents(int limit) {
        LambdaQueryWrapper<OutboxEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboxEvent::getStatus, OutboxEvent.EventStatus.PENDING.getCode())
                   .orderByAsc(OutboxEvent::getCreatedAt)
                   .last("LIMIT " + limit);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public void markAsSent(String eventId) {
        LambdaQueryWrapper<OutboxEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboxEvent::getEventId, eventId);
        
        OutboxEvent event = new OutboxEvent();
        event.setStatus(OutboxEvent.EventStatus.SENT.getCode());
        event.setSentAt(LocalDateTime.now());
        
        update(event, queryWrapper);
    }

    @Override
    @Transactional
    public void markAsFailed(String eventId, String errorMessage) {
        LambdaQueryWrapper<OutboxEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboxEvent::getEventId, eventId);
        
        OutboxEvent event = new OutboxEvent();
        event.setStatus(OutboxEvent.EventStatus.FAILED.getCode());
        event.setErrorMessage(errorMessage);
        
        update(event, queryWrapper);
    }

    @Override
    @Transactional
    public void incrementRetryCount(String eventId) {
        LambdaQueryWrapper<OutboxEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboxEvent::getEventId, eventId);
        
        OutboxEvent existingEvent = getOne(queryWrapper);
        if (existingEvent != null) {
            OutboxEvent updateEvent = new OutboxEvent();
            updateEvent.setRetryCount(existingEvent.getRetryCount() + 1);
            updateEvent.setNextRetryAt(LocalDateTime.now().plusMinutes(5)); // 5分钟后重试
            
            update(updateEvent, queryWrapper);
        }
    }

    @Override
    public List<OutboxEvent> getRetryEvents(int limit) {
        LambdaQueryWrapper<OutboxEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboxEvent::getStatus, OutboxEvent.EventStatus.FAILED.getCode())
                   .lt(OutboxEvent::getNextRetryAt, LocalDateTime.now())
                   .lt(OutboxEvent::getRetryCount, 3) // 最多重试3次
                   .orderByAsc(OutboxEvent::getNextRetryAt)
                   .last("LIMIT " + limit);
        
        return list(queryWrapper);
    }
}
