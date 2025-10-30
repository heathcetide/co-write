package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Outbox事件表 - 存储待发送的事件
 * 实现Outbox模式，确保事件最终一致性
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_outbox_event")
public class OutboxEvent extends BaseEntity {

    /**
     * 事件唯一ID
     */
    @TableField("event_id")
    private String eventId;

    /**
     * 事件类型（DOCUMENT_EDIT、AI_CALL、COLLABORATION等）
     */
    @TableField("event_type")
    private String eventType;

    /**
     * 聚合根ID（如文档ID、用户ID）
     */
    @TableField("aggregate_id")
    private String aggregateId;

    /**
     * 事件数据（JSON格式）
     */
    @TableField("event_data")
    private String eventData;

    /**
     * 状态（PENDING/SENT/FAILED）
     */
    @TableField("status")
    private String status;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retries")
    private Integer maxRetries;

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
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 事件状态枚举
     */
    public enum EventStatus {
        PENDING("PENDING", "待发送"),
        SENT("SENT", "已发送"),
        FAILED("FAILED", "发送失败");

        private final String code;
        private final String description;

        EventStatus(String code, String description) {
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

    /**
     * 事件类型枚举
     */
    public enum EventType {
        DOCUMENT_EDIT("DOCUMENT_EDIT", "文档编辑"),
        DOCUMENT_CREATE("DOCUMENT_CREATE", "文档创建"),
        DOCUMENT_DELETE("DOCUMENT_DELETE", "文档删除"),
        AI_CALL("AI_CALL", "AI调用"),
        COLLABORATION("COLLABORATION", "协作操作"),
        STORAGE_USAGE("STORAGE_USAGE", "存储使用"),
        USER_LOGIN("USER_LOGIN", "用户登录"),
        USER_REGISTER("USER_REGISTER", "用户注册");

        private final String code;
        private final String description;

        EventType(String code, String description) {
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
