package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.UsageMetricsMapper;
import com.cowrite.project.model.entity.UsageMetrics;
import com.cowrite.project.service.UsageAggregatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 用量聚合服务实现
 *
 * @author heathcetide
 */
@Service
public class UsageAggregatorServiceImpl extends ServiceImpl<UsageMetricsMapper, UsageMetrics> implements UsageAggregatorService {

    private static final Logger logger = LoggerFactory.getLogger(UsageAggregatorServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
//    @KafkaListener(topics = "cowrite.document.events", groupId = "usage-aggregator")
    public void aggregateDocumentEditEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String documentId = event.get("documentId").asText();
            String operation = event.get("operation").asText();
            
            // 根据操作类型确定指标类型
            String metricType;
            switch (operation) {
                case "CREATE":
                    metricType = UsageMetrics.MetricType.DOCUMENT_CREATE.getCode();
                    break;
                case "DELETE":
                    metricType = UsageMetrics.MetricType.DOCUMENT_DELETE.getCode();
                    break;
                default:
                    metricType = UsageMetrics.MetricType.DOCUMENT_EDIT.getCode();
                    break;
            }
            
            // 更新日统计
            updateUsageMetrics(userId, null, metricType, BigDecimal.ONE, 
                             UsageMetrics.MetricType.DOCUMENT_EDIT.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            logger.info("聚合文档编辑事件: userId={}, documentId={}, operation={}", userId, documentId, operation);
        } catch (JsonProcessingException e) {
            logger.error("解析文档编辑事件失败: {}", eventData, e);
        }
    }

    @Override
//    @KafkaListener(topics = "cowrite.ai.events", groupId = "usage-aggregator")
    public void aggregateAiCallEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String documentId = event.get("documentId").asText();
            Integer tokens = event.get("tokens").asInt();
            String model = event.get("model").asText();
            
            // 更新AI调用次数
            updateUsageMetrics(userId, null, UsageMetrics.MetricType.AI_CALL.getCode(), 
                             BigDecimal.ONE, UsageMetrics.MetricType.AI_CALL.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            // 更新AI Token消耗
            updateUsageMetrics(userId, null, UsageMetrics.MetricType.AI_TOKENS.getCode(), 
                             BigDecimal.valueOf(tokens), UsageMetrics.MetricType.AI_TOKENS.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            logger.info("聚合AI调用事件: userId={}, documentId={}, tokens={}, model={}", userId, documentId, tokens, model);
        } catch (JsonProcessingException e) {
            logger.error("解析AI调用事件失败: {}", eventData, e);
        }
    }

    @Override
//    @KafkaListener(topics = "cowrite.collaboration.events", groupId = "usage-aggregator")
    public void aggregateCollaborationEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String documentId = event.get("documentId").asText();
            String operation = event.get("operation").asText();
            
            updateUsageMetrics(userId, null, UsageMetrics.MetricType.COLLABORATION.getCode(), 
                             BigDecimal.ONE, UsageMetrics.MetricType.COLLABORATION.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            logger.info("聚合协作事件: userId={}, documentId={}, operation={}", userId, documentId, operation);
        } catch (JsonProcessingException e) {
            logger.error("解析协作事件失败: {}", eventData, e);
        }
    }

    @Override
//    @KafkaListener(topics = "cowrite.storage.events", groupId = "usage-aggregator")
    public void aggregateStorageUsageEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String documentId = event.get("documentId").asText();
            Long bytes = event.get("bytes").asLong();
            String operation = event.get("operation").asText();
            
            // 存储使用量按字节计算
            BigDecimal storageBytes = BigDecimal.valueOf(bytes);
            if ("DELETE".equals(operation)) {
                storageBytes = storageBytes.negate(); // 删除时减少存储量
            }
            
            updateUsageMetrics(userId, null, UsageMetrics.MetricType.STORAGE_USAGE.getCode(), 
                             storageBytes, UsageMetrics.MetricType.STORAGE_USAGE.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            logger.info("聚合存储使用事件: userId={}, documentId={}, bytes={}, operation={}", userId, documentId, bytes, operation);
        } catch (JsonProcessingException e) {
            logger.error("解析存储使用事件失败: {}", eventData, e);
        }
    }

    @Override
//    @KafkaListener(topics = "cowrite.user.events", groupId = "usage-aggregator")
    public void aggregateUserLoginEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String ip = event.get("ip").asText();
            
            updateUsageMetrics(userId, null, UsageMetrics.MetricType.LOGIN_COUNT.getCode(), 
                             BigDecimal.ONE, UsageMetrics.MetricType.LOGIN_COUNT.getUnit(),
                             UsageMetrics.Period.DAILY.getCode(), 
                             LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            
            logger.info("聚合用户登录事件: userId={}, ip={}", userId, ip);
        } catch (JsonProcessingException e) {
            logger.error("解析用户登录事件失败: {}", eventData, e);
        }
    }

    @Override
//    @KafkaListener(topics = "cowrite.user.events", groupId = "usage-aggregator")
    public void aggregateUserRegisterEvent(String eventData) {
        try {
            JsonNode event = objectMapper.readTree(eventData);
            Long userId = event.get("userId").asLong();
            String source = event.get("source").asText();
            
            // 用户注册事件通常不需要统计用量，但可以用于其他目的
            logger.info("聚合用户注册事件: userId={}, source={}", userId, source);
        } catch (JsonProcessingException e) {
            logger.error("解析用户注册事件失败: {}", eventData, e);
        }
    }

    @Override
    public UsageMetrics getUserUsageMetrics(Long userId, String metricType, String period, String periodDate) {
        LambdaQueryWrapper<UsageMetrics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UsageMetrics::getUserId, userId)
                   .eq(UsageMetrics::getMetricType, metricType)
                   .eq(UsageMetrics::getPeriod, period)
                   .eq(UsageMetrics::getPeriodDate, periodDate);
        
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void updateUsageMetrics(Long userId, Long organizationId, String metricType, 
                                  BigDecimal metricValue, String unit, 
                                  String period, String periodDate) {
        LambdaQueryWrapper<UsageMetrics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UsageMetrics::getUserId, userId)
                   .eq(UsageMetrics::getMetricType, metricType)
                   .eq(UsageMetrics::getPeriod, period)
                   .eq(UsageMetrics::getPeriodDate, periodDate);
        
        UsageMetrics existing = getOne(queryWrapper);
        
        if (existing != null) {
            // 更新现有记录
            existing.setMetricValue(existing.getMetricValue().add(metricValue));
            updateById(existing);
        } else {
            // 创建新记录
            UsageMetrics newMetric = new UsageMetrics();
            newMetric.setUserId(userId);
            newMetric.setOrganizationId(organizationId);
            newMetric.setMetricType(metricType);
            newMetric.setMetricValue(metricValue);
            newMetric.setUnit(unit);
            newMetric.setPeriod(period);
            newMetric.setPeriodDate(LocalDate.parse(periodDate));
            save(newMetric);
        }
    }
}
