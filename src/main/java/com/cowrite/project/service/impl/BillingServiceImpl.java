package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.BillingInvoiceMapper;
import com.cowrite.project.mapper.BillingInvoiceItemMapper;
import com.cowrite.project.mapper.BillingRuleMapper;
import com.cowrite.project.mapper.UsageMetricsMapper;
import com.cowrite.project.model.entity.*;
import com.cowrite.project.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 计费服务实现
 *
 * @author heathcetide
 */
@Service
public class BillingServiceImpl extends ServiceImpl<BillingInvoiceMapper, BillingInvoice> implements BillingService {

    private static final Logger logger = LoggerFactory.getLogger(BillingServiceImpl.class);

    @Autowired
    private BillingInvoiceItemMapper billingInvoiceItemMapper;

    @Autowired
    private BillingRuleMapper billingRuleMapper;

    @Autowired
    private UsageMetricsMapper usageMetricsMapper;

    @Override
    @Transactional
    public Long generateMonthlyInvoice(Long userId, int year, int month) {
        LocalDate periodStart = LocalDate.of(year, month, 1);
        LocalDate periodEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());
        
        // 检查是否已存在该月的账单
        LambdaQueryWrapper<BillingInvoice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingInvoice::getUserId, userId)
                   .eq(BillingInvoice::getBillingPeriod, BillingInvoice.BillingPeriod.MONTHLY.getCode())
                   .eq(BillingInvoice::getPeriodStart, periodStart);
        
        BillingInvoice existingInvoice = getOne(queryWrapper);
        if (existingInvoice != null) {
            logger.warn("用户 {} 的 {} 月账单已存在", userId, month);
            return existingInvoice.getId();
        }
        
        // 创建新账单
        BillingInvoice invoice = new BillingInvoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setUserId(userId);
        invoice.setBillingPeriod(BillingInvoice.BillingPeriod.MONTHLY.getCode());
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);
        invoice.setStatus(BillingInvoice.InvoiceStatus.PENDING.getCode());
        invoice.setDueDate(periodEnd.plusDays(30)); // 30天后到期
        
        save(invoice);
        
        // 获取该月的用量统计
        List<UsageMetrics> usageMetrics = getUsageMetricsForPeriod(userId, periodStart, periodEnd);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 为每个用量统计创建账单明细
        for (UsageMetrics usage : usageMetrics) {
            BillingRule rule = getBillingRule(usage.getMetricType(), periodStart);
            if (rule != null && rule.getIsActive()) {
                BigDecimal cost = calculateUsageCost(usage, rule);
                
                BillingInvoiceItem item = new BillingInvoiceItem();
                item.setInvoiceId(invoice.getId());
                item.setMetricType(usage.getMetricType());
                item.setUsageAmount(usage.getMetricValue());
                item.setUnitPrice(rule.getUnitPrice());
                item.setTotalAmount(cost);
                item.setDescription(String.format("%s - %s %s", 
                    getMetricTypeDescription(usage.getMetricType()),
                    usage.getMetricValue(),
                    usage.getUnit()));
                
                billingInvoiceItemMapper.insert(item);
                totalAmount = totalAmount.add(cost);
            }
        }
        
        // 更新账单总金额
        invoice.setTotalAmount(totalAmount);
        updateById(invoice);
        
        logger.info("为用户 {} 生成 {} 月账单，总金额: {}", userId, month, totalAmount);
        return invoice.getId();
    }

    @Override
    @Transactional
    public Long generateYearlyInvoice(Long userId, int year) {
        LocalDate periodStart = LocalDate.of(year, 1, 1);
        LocalDate periodEnd = LocalDate.of(year, 12, 31);
        
        // 检查是否已存在该年的账单
        LambdaQueryWrapper<BillingInvoice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingInvoice::getUserId, userId)
                   .eq(BillingInvoice::getBillingPeriod, BillingInvoice.BillingPeriod.YEARLY.getCode())
                   .eq(BillingInvoice::getPeriodStart, periodStart);
        
        BillingInvoice existingInvoice = getOne(queryWrapper);
        if (existingInvoice != null) {
            logger.warn("用户 {} 的 {} 年账单已存在", userId, year);
            return existingInvoice.getId();
        }
        
        // 创建新账单
        BillingInvoice invoice = new BillingInvoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setUserId(userId);
        invoice.setBillingPeriod(BillingInvoice.BillingPeriod.YEARLY.getCode());
        invoice.setPeriodStart(periodStart);
        invoice.setPeriodEnd(periodEnd);
        invoice.setStatus(BillingInvoice.InvoiceStatus.PENDING.getCode());
        invoice.setDueDate(periodEnd.plusDays(30)); // 30天后到期
        
        save(invoice);
        
        // 获取该年的用量统计
        List<UsageMetrics> usageMetrics = getUsageMetricsForPeriod(userId, periodStart, periodEnd);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 为每个用量统计创建账单明细
        for (UsageMetrics usage : usageMetrics) {
            BillingRule rule = getBillingRule(usage.getMetricType(), periodStart);
            if (rule != null && rule.getIsActive()) {
                BigDecimal cost = calculateUsageCost(usage, rule);
                
                BillingInvoiceItem item = new BillingInvoiceItem();
                item.setInvoiceId(invoice.getId());
                item.setMetricType(usage.getMetricType());
                item.setUsageAmount(usage.getMetricValue());
                item.setUnitPrice(rule.getUnitPrice());
                item.setTotalAmount(cost);
                item.setDescription(String.format("%s - %s %s", 
                    getMetricTypeDescription(usage.getMetricType()),
                    usage.getMetricValue(),
                    usage.getUnit()));
                
                billingInvoiceItemMapper.insert(item);
                totalAmount = totalAmount.add(cost);
            }
        }
        
        // 更新账单总金额
        invoice.setTotalAmount(totalAmount);
        updateById(invoice);
        
        logger.info("为用户 {} 生成 {} 年账单，总金额: {}", userId, year, totalAmount);
        return invoice.getId();
    }

    @Override
    public BigDecimal calculateUsageCost(UsageMetrics usageMetrics) {
        BillingRule rule = getBillingRule(usageMetrics.getMetricType(), usageMetrics.getPeriodDate());
        if (rule == null || !rule.getIsActive()) {
            return BigDecimal.ZERO;
        }
        
        return calculateUsageCost(usageMetrics, rule);
    }

    @Override
    public BillingRule getBillingRule(String metricType, LocalDate date) {
        LambdaQueryWrapper<BillingRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingRule::getMetricType, metricType)
                   .eq(BillingRule::getIsActive, true)
                   .le(BillingRule::getEffectiveFrom, date)
                   .and(wrapper -> wrapper.isNull(BillingRule::getEffectiveTo)
                                  .or()
                                  .ge(BillingRule::getEffectiveTo, date))
                   .orderByDesc(BillingRule::getEffectiveFrom)
                   .last("LIMIT 1");
        
        return billingRuleMapper.selectOne(queryWrapper);
    }

    @Override
    public List<BillingInvoice> getUserInvoices(Long userId, String status) {
        LambdaQueryWrapper<BillingInvoice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingInvoice::getUserId, userId);
        if (status != null) {
            queryWrapper.eq(BillingInvoice::getStatus, status);
        }
        queryWrapper.orderByDesc(BillingInvoice::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean payInvoice(Long invoiceId, BigDecimal amount) {
        BillingInvoice invoice = getById(invoiceId);
        if (invoice == null) {
            return false;
        }
        
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(amount);
        invoice.setPaidAmount(newPaidAmount);
        
        if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(BillingInvoice.InvoiceStatus.PAID.getCode());
            invoice.setPaidAt(java.time.LocalDateTime.now());
        }
        
        updateById(invoice);
        return true;
    }

    @Override
    public BillingInvoice getInvoiceDetail(Long invoiceId) {
        return getById(invoiceId);
    }

    /**
     * 获取指定期间的用量统计
     */
    private List<UsageMetrics> getUsageMetricsForPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<UsageMetrics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UsageMetrics::getUserId, userId)
                   .ge(UsageMetrics::getPeriodDate, startDate)
                   .le(UsageMetrics::getPeriodDate, endDate)
                   .eq(UsageMetrics::getPeriod, UsageMetrics.Period.DAILY.getCode());
        
        return usageMetricsMapper.selectList(queryWrapper);
    }

    /**
     * 计算用量费用
     */
    private BigDecimal calculateUsageCost(UsageMetrics usage, BillingRule rule) {
        BigDecimal usageAmount = usage.getMetricValue();
        
        // 扣除免费额度
        if (rule.getFreeQuota() != null && rule.getFreeQuota().compareTo(BigDecimal.ZERO) > 0) {
            usageAmount = usageAmount.subtract(rule.getFreeQuota());
            if (usageAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }
        }
        
        // 按单价计算费用
        return usageAmount.multiply(rule.getUnitPrice());
    }

    /**
     * 生成账单号
     */
    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * 获取指标类型描述
     */
    private String getMetricTypeDescription(String metricType) {
        for (UsageMetrics.MetricType type : UsageMetrics.MetricType.values()) {
            if (type.getCode().equals(metricType)) {
                return type.getDescription();
            }
        }
        return metricType;
    }
}
