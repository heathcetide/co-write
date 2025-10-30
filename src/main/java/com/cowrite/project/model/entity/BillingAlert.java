package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 阈值预警配置表
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_alert")
public class BillingAlert extends BaseEntity {

    /**
     * WebHookId
     */
    @TableId
    private Long id;

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
     * 指标类型
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 阈值
     */
    @TableField("threshold_value")
    private BigDecimal thresholdValue;

    /**
     * 预警类型（USAGE、COST）
     */
    @TableField("alert_type")
    private String alertType;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 预警类型枚举
     */
    public enum AlertType {
        USAGE("USAGE", "用量预警"),
        COST("COST", "费用预警");

        private final String code;
        private final String description;

        AlertType(String code, String description) {
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
