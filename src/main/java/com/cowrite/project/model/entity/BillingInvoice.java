package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账单表 - 用户账单记录
 *
 * @author heathcetide
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hib_billing_invoice")
public class BillingInvoice extends BaseEntity {

    /**
     * WebHookId
     */
    @TableId
    private Long id;

    /**
     * 账单号
     */
    @TableField("invoice_number")
    private String invoiceNumber;

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
     * 计费周期（monthly、yearly）
     */
    @TableField("billing_period")
    private String billingPeriod;

    /**
     * 周期开始日期
     */
    @TableField("period_start")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodStart;

    /**
     * 周期结束日期
     */
    @TableField("period_end")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodEnd;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 已付金额
     */
    @TableField("paid_amount")
    private BigDecimal paidAmount;

    /**
     * 状态（PENDING、PAID、OVERDUE、CANCELLED）
     */
    @TableField("status")
    private String status;

    /**
     * 到期日期
     */
    @TableField("due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /**
     * 支付时间
     */
    @TableField("paid_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidAt;

    /**
     * 账单状态枚举
     */
    public enum InvoiceStatus {
        PENDING("PENDING", "待支付"),
        PAID("PAID", "已支付"),
        OVERDUE("OVERDUE", "逾期"),
        CANCELLED("CANCELLED", "已取消");

        private final String code;
        private final String description;

        InvoiceStatus(String code, String description) {
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
     * 计费周期枚举
     */
    public enum BillingPeriod {
        MONTHLY("monthly", "月计费"),
        YEARLY("yearly", "年计费");

        private final String code;
        private final String description;

        BillingPeriod(String code, String description) {
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
