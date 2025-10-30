package com.cowrite.project.service;

import com.cowrite.project.model.entity.BillingInvoice;
import com.cowrite.project.model.entity.BillingRule;
import com.cowrite.project.model.entity.UsageMetrics;

import java.time.LocalDate;
import java.util.List;

/**
 * 计费服务接口
 *
 * @author heathcetide
 */
public interface BillingService {

    /**
     * 生成月度账单
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 账单ID
     */
    Long generateMonthlyInvoice(Long userId, int year, int month);

    /**
     * 生成年度账单
     *
     * @param userId 用户ID
     * @param year   年份
     * @return 账单ID
     */
    Long generateYearlyInvoice(Long userId, int year);

    /**
     * 计算用量费用
     *
     * @param usageMetrics 用量统计
     * @return 费用
     */
    java.math.BigDecimal calculateUsageCost(UsageMetrics usageMetrics);

    /**
     * 获取计费规则
     *
     * @param metricType 指标类型
     * @param date       日期
     * @return 计费规则
     */
    BillingRule getBillingRule(String metricType, LocalDate date);

    /**
     * 获取用户账单列表
     *
     * @param userId 用户ID
     * @param status 账单状态
     * @return 账单列表
     */
    List<BillingInvoice> getUserInvoices(Long userId, String status);

    /**
     * 支付账单
     *
     * @param invoiceId 账单ID
     * @param amount    支付金额
     * @return 是否成功
     */
    boolean payInvoice(Long invoiceId, java.math.BigDecimal amount);

    /**
     * 获取账单详情
     *
     * @param invoiceId 账单ID
     * @return 账单详情
     */
    BillingInvoice getInvoiceDetail(Long invoiceId);
}
