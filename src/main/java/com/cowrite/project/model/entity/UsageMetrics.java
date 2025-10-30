package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用量统计表 - 按用户和类型统计用量
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_usage_metrics")
public class UsageMetrics extends BaseEntity {

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 指标类型（DOCUMENT_EDIT、AI_CALL、COLLABORATION、STORAGE等）
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 指标数值
     */
    @TableField("metric_value")
    private BigDecimal metricValue;

    /**
     * 单位（count、minutes、bytes等）
     */
    @TableField("unit")
    private String unit;

    /**
     * 统计周期（daily、monthly）
     */
    @TableField("period")
    private String period;

    /**
     * 统计日期
     */
    @TableField("period_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodDate;

    /**
     * 指标类型枚举
     */
    public enum MetricType {
        DOCUMENT_EDIT("DOCUMENT_EDIT", "文档编辑次数", "count"),
        DOCUMENT_CREATE("DOCUMENT_CREATE", "文档创建次数", "count"),
        DOCUMENT_DELETE("DOCUMENT_DELETE", "文档删除次数", "count"),
        AI_CALL("AI_CALL", "AI调用次数", "count"),
        AI_TOKENS("AI_TOKENS", "AI消耗Token数", "count"),
        COLLABORATION("COLLABORATION", "协作操作次数", "count"),
        STORAGE_USAGE("STORAGE_USAGE", "存储使用量", "bytes"),
        LOGIN_COUNT("LOGIN_COUNT", "登录次数", "count"),
        API_CALLS("API_CALLS", "API调用次数", "count");

        private final String code;
        private final String description;
        private final String unit;

        MetricType(String code, String description, String unit) {
            this.code = code;
            this.description = description;
            this.unit = unit;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public String getUnit() {
            return unit;
        }
    }

    /**
     * 统计周期枚举
     */
    public enum Period {
        DAILY("daily", "日统计"),
        MONTHLY("monthly", "月统计"),
        YEARLY("yearly", "年统计");

        private final String code;
        private final String description;

        Period(String code, String description) {
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
