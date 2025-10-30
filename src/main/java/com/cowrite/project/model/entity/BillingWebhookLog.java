package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Webhook发送记录表 - 记录Webhook发送历史
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_webhook_log")
public class BillingWebhookLog extends BaseEntity {

    /**
     * WebHookId
     */
    @TableId
    private Long id;

    /**
     * Webhook配置ID
     */
    @TableField("webhook_id")
    private Long webhookId;

    /**
     * 事件类型
     */
    @TableField("event_type")
    private String eventType;

    /**
     * 发送的数据（JSON格式）
     */
    @TableField("payload")
    private String payload;

    /**
     * 响应状态码
     */
    @TableField("response_code")
    private Integer responseCode;

    /**
     * 响应内容
     */
    @TableField("response_body")
    private String responseBody;

    /**
     * 状态（SUCCESS、FAILED、RETRYING）
     */
    @TableField("status")
    private String status;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    @TableField("next_retry_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextRetryAt;

    /**
     * 发送时间
     */
    @TableField("sent_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    /**
     * 状态枚举
     */
    public enum WebhookStatus {
        SUCCESS("SUCCESS", "发送成功"),
        FAILED("FAILED", "发送失败"),
        RETRYING("RETRYING", "重试中");

        private final String code;
        private final String description;

        WebhookStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
