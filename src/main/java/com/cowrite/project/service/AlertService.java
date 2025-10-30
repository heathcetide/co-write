package com.cowrite.project.service;

import com.cowrite.project.model.entity.BillingAlert;
import com.cowrite.project.model.entity.UsageMetrics;

import java.util.List;

/**
 * 预警服务接口
 *
 * @author heathcetide
 */
public interface AlertService {

    /**
     * 检查用量预警
     *
     * @param userId 用户ID
     */
    void checkUsageAlerts(Long userId);

    /**
     * 检查所有用户的用量预警
     */
    void checkAllUsageAlerts();

    /**
     * 创建用量预警配置
     *
     * @param alert 预警配置
     * @return 预警ID
     */
    Long createAlert(BillingAlert alert);

    /**
     * 更新预警配置
     *
     * @param alert 预警配置
     * @return 是否成功
     */
    boolean updateAlert(BillingAlert alert);

    /**
     * 删除预警配置
     *
     * @param alertId 预警ID
     * @return 是否成功
     */
    boolean deleteAlert(Long alertId);

    /**
     * 获取用户的预警配置
     *
     * @param userId 用户ID
     * @return 预警配置列表
     */
    List<BillingAlert> getUserAlerts(Long userId);

    /**
     * 检查特定指标的预警
     *
     * @param userId     用户ID
     * @param metricType 指标类型
     * @param currentValue 当前值
     */
    void checkMetricAlert(Long userId, String metricType, java.math.BigDecimal currentValue);
}
