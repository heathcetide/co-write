package com.cowrite.project.service;

import com.cowrite.project.model.entity.UsageMetrics;

/**
 * 用量聚合服务接口
 *
 * @author heathcetide
 */
public interface UsageAggregatorService {

    /**
     * 聚合文档编辑事件
     *
     * @param eventData 事件数据
     */
    void aggregateDocumentEditEvent(String eventData);

    /**
     * 聚合AI调用事件
     *
     * @param eventData 事件数据
     */
    void aggregateAiCallEvent(String eventData);

    /**
     * 聚合协作事件
     *
     * @param eventData 事件数据
     */
    void aggregateCollaborationEvent(String eventData);

    /**
     * 聚合存储使用事件
     *
     * @param eventData 事件数据
     */
    void aggregateStorageUsageEvent(String eventData);

    /**
     * 聚合用户登录事件
     *
     * @param eventData 事件数据
     */
    void aggregateUserLoginEvent(String eventData);

    /**
     * 聚合用户注册事件
     *
     * @param eventData 事件数据
     */
    void aggregateUserRegisterEvent(String eventData);

    /**
     * 获取用户用量统计
     *
     * @param userId     用户ID
     * @param metricType 指标类型
     * @param period     统计周期
     * @param periodDate 统计日期
     * @return 用量统计
     */
    UsageMetrics getUserUsageMetrics(Long userId, String metricType, String period, String periodDate);

    /**
     * 更新用量统计
     *
     * @param userId      用户ID
     * @param organizationId 组织ID
     * @param metricType  指标类型
     * @param metricValue 指标数值
     * @param unit        单位
     * @param period      统计周期
     * @param periodDate  统计日期
     */
    void updateUsageMetrics(Long userId, Long organizationId, String metricType, 
                           java.math.BigDecimal metricValue, String unit, 
                           String period, String periodDate);
}
