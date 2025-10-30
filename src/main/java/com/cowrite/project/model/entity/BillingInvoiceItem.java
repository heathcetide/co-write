package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账单明细表 - 账单详细项目
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_invoice_item")
public class BillingInvoiceItem extends BaseEntity {

    /**
     * 账单ID
     */
    @TableField("invoice_id")
    private Long invoiceId;

    /**
     * 指标类型
     */
    @TableField("metric_type")
    private String metricType;

    /**
     * 使用量
     */
    @TableField("usage_amount")
    private BigDecimal usageAmount;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 小计金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 描述
     */
    @TableField("description")
    private String description;
}
