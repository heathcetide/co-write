package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.entity.BillingWebhook;
import com.cowrite.project.model.entity.BillingWebhookLog;

import java.util.List;

/**
 * Webhook服务接口
 *
 * @author heathcetide
 */
public interface WebhookService extends IService<BillingWebhook> {

    /**
     * 发送账单创建Webhook
     *
     * @param invoiceId 账单ID
     */
    void sendInvoiceCreatedWebhook(Long invoiceId);

    /**
     * 发送账单支付Webhook
     *
     * @param invoiceId 账单ID
     */
    void sendInvoicePaidWebhook(Long invoiceId);

    /**
     * 发送账单逾期Webhook
     *
     * @param invoiceId 账单ID
     */
    void sendInvoiceOverdueWebhook(Long invoiceId);

    /**
     * 发送用量预警Webhook
     *
     * @param userId     用户ID
     * @param metricType 指标类型
     * @param currentValue 当前值
     * @param thresholdValue 阈值
     */
    void sendUsageAlertWebhook(Long userId, String metricType, java.math.BigDecimal currentValue, java.math.BigDecimal thresholdValue);

    /**
     * 获取Webhook配置
     *
     * @param organizationId 组织ID
     * @return Webhook配置列表
     */
    List<BillingWebhook> getWebhookConfigs(Long organizationId);

    /**
     * 创建Webhook配置
     *
     * @param webhook Webhook配置
     * @return 配置ID
     */
    Long createWebhookConfig(BillingWebhook webhook);

    /**
     * 更新Webhook配置
     *
     * @param webhook Webhook配置
     * @return 是否成功
     */
    boolean updateWebhookConfig(BillingWebhook webhook);

    /**
     * 删除Webhook配置
     *
     * @param webhookId Webhook配置ID
     * @return 是否成功
     */
    boolean deleteWebhookConfig(Long webhookId);

    /**
     * 获取Webhook发送记录
     *
     * @param webhookId Webhook配置ID
     * @return 发送记录列表
     */
    List<BillingWebhookLog> getWebhookLogs(Long webhookId);
}