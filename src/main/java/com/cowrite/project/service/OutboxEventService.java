package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.entity.OutboxEvent;

import java.util.List;

/**
 * Outbox事件服务接口
 *
 * @author heathcetide
 */
public interface OutboxEventService extends IService<OutboxEvent> {

    /**
     * 保存事件到Outbox
     *
     * @param eventType   事件类型
     * @param aggregateId 聚合根ID
     * @param eventData   事件数据
     * @return 事件ID
     */
    String saveEvent(String eventType, String aggregateId, Object eventData);

    /**
     * 获取待发送的事件
     *
     * @param limit 限制数量
     * @return 待发送事件列表
     */
    List<OutboxEvent> getPendingEvents(int limit);

    /**
     * 标记事件为已发送
     *
     * @param eventId 事件ID
     */
    void markAsSent(String eventId);

    /**
     * 标记事件为发送失败
     *
     * @param eventId      事件ID
     * @param errorMessage 错误信息
     */
    void markAsFailed(String eventId, String errorMessage);

    /**
     * 增加重试次数
     *
     * @param eventId 事件ID
     */
    void incrementRetryCount(String eventId);

    /**
     * 获取需要重试的事件
     *
     * @param limit 限制数量
     * @return 需要重试的事件列表
     */
    List<OutboxEvent> getRetryEvents(int limit);
}
