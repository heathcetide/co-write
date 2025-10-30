package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.BillingAlertMapper;
import com.cowrite.project.mapper.UsageMetricsMapper;
import com.cowrite.project.model.entity.BillingAlert;
import com.cowrite.project.model.entity.UsageMetrics;
import com.cowrite.project.service.AlertService;
import com.cowrite.project.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 预警服务实现
 *
 * @author heathcetide
 */
@Service
public class AlertServiceImpl extends ServiceImpl<BillingAlertMapper, BillingAlert> implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    @Autowired
    private UsageMetricsMapper usageMetricsMapper;

    @Autowired
    private WebhookService webhookService;

    @Override
    @Scheduled(fixedDelay = 300000) // 每5分钟检查一次
    public void checkAllUsageAlerts() {
        try {
            // 获取所有启用的预警配置
            LambdaQueryWrapper<BillingAlert> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BillingAlert::getIsActive, true);
            List<BillingAlert> alerts = list(queryWrapper);

            for (BillingAlert alert : alerts) {
                checkUsageAlerts(alert.getUserId());
            }
        } catch (Exception e) {
            logger.error("检查用量预警时发生错误", e);
        }
    }

    @Override
    public void checkUsageAlerts(Long userId) {
        // 获取用户的所有预警配置
        List<BillingAlert> alerts = getUserAlerts(userId);
        
        for (BillingAlert alert : alerts) {
            if (!alert.getIsActive()) {
                continue;
            }
            
            // 获取当前用量
            BigDecimal currentValue = getCurrentUsage(userId, alert.getMetricType());
            if (currentValue != null) {
                checkMetricAlert(userId, alert.getMetricType(), currentValue);
            }
        }
    }

    @Override
    @Transactional
    public Long createAlert(BillingAlert alert) {
        save(alert);
        return alert.getId();
    }

    @Override
    @Transactional
    public boolean updateAlert(BillingAlert alert) {
        return updateById(alert);
    }

    @Override
    @Transactional
    public boolean deleteAlert(Long alertId) {
        return removeById(alertId);
    }

    @Override
    public List<BillingAlert> getUserAlerts(Long userId) {
        LambdaQueryWrapper<BillingAlert> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingAlert::getUserId, userId)
                   .eq(BillingAlert::getIsActive, true)
                   .orderByDesc(BillingAlert::getCreatedAt);
        
        return list(queryWrapper);
    }

    @Override
    public void checkMetricAlert(Long userId, String metricType, BigDecimal currentValue) {
        // 获取该指标的预警配置
        LambdaQueryWrapper<BillingAlert> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingAlert::getUserId, userId)
                   .eq(BillingAlert::getMetricType, metricType)
                   .eq(BillingAlert::getIsActive, true);
        
        List<BillingAlert> alerts = list(queryWrapper);
        
        for (BillingAlert alert : alerts) {
            if (currentValue.compareTo(alert.getThresholdValue()) >= 0) {
                // 触发预警
                logger.warn("用户 {} 的 {} 指标超过阈值: 当前值={}, 阈值={}", 
                           userId, metricType, currentValue, alert.getThresholdValue());
                
                // 发送Webhook通知
                webhookService.sendUsageAlertWebhook(userId, metricType, currentValue, alert.getThresholdValue());
            }
        }
    }

    /**
     * 获取当前用量
     */
    private BigDecimal getCurrentUsage(Long userId, String metricType) {
        // 获取今日用量
        LambdaQueryWrapper<UsageMetrics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UsageMetrics::getUserId, userId)
                   .eq(UsageMetrics::getMetricType, metricType)
                   .eq(UsageMetrics::getPeriod, UsageMetrics.Period.DAILY.getCode())
                   .eq(UsageMetrics::getPeriodDate, LocalDate.now());
        
        UsageMetrics usage = usageMetricsMapper.selectOne(queryWrapper);
        return usage != null ? usage.getMetricValue() : BigDecimal.ZERO;
    }
}
