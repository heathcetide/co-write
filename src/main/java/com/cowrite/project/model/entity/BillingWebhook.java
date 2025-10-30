package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Webhook配置表 - 账单通知配置
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_webhook")
public class BillingWebhook extends BaseEntity {

    /**
     * WebHookId
     */
    @TableId
    private Long id;

    /**
     * 组织ID（为空表示全局配置）
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * Webhook URL
     */
    @TableField("webhook_url")
    private String webhookUrl;

    /**
     * 签名密钥
     */
    @TableField("secret_key")
    private String secretKey;

    /**
     * 订阅的事件类型（JSON格式）
     */
    @TableField("events")
    private String events;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 超时时间（秒）
     */
    @TableField("timeout_seconds")
    private Integer timeoutSeconds;
}
