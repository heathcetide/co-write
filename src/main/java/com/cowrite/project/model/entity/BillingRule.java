package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 计费规则表 - 定义不同指标的计费规则
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_rules")
public class BillingRule extends BaseEntity {

    /**
     * WebHookId
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;

    /**
     * 指标类型
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 计费类型（PER_UNIT、TIERED、FIXED）
     */
    @TableField("billing_type")
    private String billingType;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 免费额度
     */
    @TableField("free_quota")
    private BigDecimal freeQuota;

    /**
     * 阶梯计费规则（JSON格式）
     */
    @TableField("tier_rules")
    private String tierRules;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 生效时间
     */
    @TableField("effective_from")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveFrom;

    /**
     * 失效时间
     */
    @TableField("effective_to")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime effectiveTo;

    /**
     * 计费类型枚举
     */
    public enum BillingType {
        PER_UNIT("PER_UNIT", "按量计费"),
        TIERED("TIERED", "阶梯计费"),
        FIXED("FIXED", "固定费用");

        private final String code;
        private final String description;

        BillingType(String code, String description) {
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
