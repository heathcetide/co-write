package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.BillingWebhookMapper;
import com.cowrite.project.mapper.BillingWebhookLogMapper;
import com.cowrite.project.model.entity.BillingInvoice;
import com.cowrite.project.model.entity.BillingWebhook;
import com.cowrite.project.model.entity.BillingWebhookLog;
import com.cowrite.project.service.BillingService;
import com.cowrite.project.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Webhook服务实现
 *
 * @author heathcetide
 */
@Service
public class WebhookServiceImpl extends ServiceImpl<BillingWebhookMapper, BillingWebhook> implements WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);

    @Autowired
    private BillingWebhookLogMapper webhookLogMapper;

    @Autowired
    private BillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public void sendInvoiceCreatedWebhook(Long invoiceId) {
        BillingInvoice invoice = billingService.getInvoiceDetail(invoiceId);
        if (invoice == null) {
            return;
        }

        List<BillingWebhook> webhooks = getWebhookConfigs(invoice.getOrganizationId());
        for (BillingWebhook webhook : webhooks) {
            if (webhook.getIsActive() && isEventSubscribed(webhook, "invoice.created")) {
                sendWebhook(webhook, "invoice.created", createInvoicePayload(invoice));
            }
        }
    }

    @Override
    @Transactional
    public void sendInvoicePaidWebhook(Long invoiceId) {
        BillingInvoice invoice = billingService.getInvoiceDetail(invoiceId);
        if (invoice == null) {
            return;
        }

        List<BillingWebhook> webhooks = getWebhookConfigs(invoice.getOrganizationId());
        for (BillingWebhook webhook : webhooks) {
            if (webhook.getIsActive() && isEventSubscribed(webhook, "invoice.paid")) {
                sendWebhook(webhook, "invoice.paid", createInvoicePayload(invoice));
            }
        }
    }

    @Override
    @Transactional
    public void sendInvoiceOverdueWebhook(Long invoiceId) {
        BillingInvoice invoice = billingService.getInvoiceDetail(invoiceId);
        if (invoice == null) {
            return;
        }

        List<BillingWebhook> webhooks = getWebhookConfigs(invoice.getOrganizationId());
        for (BillingWebhook webhook : webhooks) {
            if (webhook.getIsActive() && isEventSubscribed(webhook, "invoice.overdue")) {
                sendWebhook(webhook, "invoice.overdue", createInvoicePayload(invoice));
            }
        }
    }

    @Override
    @Transactional
    public void sendUsageAlertWebhook(Long userId, String metricType, java.math.BigDecimal currentValue, java.math.BigDecimal thresholdValue) {
        List<BillingWebhook> webhooks = getWebhookConfigs(null); // 全局配置
        for (BillingWebhook webhook : webhooks) {
            if (webhook.getIsActive() && isEventSubscribed(webhook, "usage.alert")) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("event", "usage.alert");
                payload.put("userId", userId);
                payload.put("metricType", metricType);
                payload.put("currentValue", currentValue);
                payload.put("thresholdValue", thresholdValue);
                payload.put("timestamp", LocalDateTime.now());
                
                sendWebhook(webhook, "usage.alert", payload);
            }
        }
    }

    @Override
    public List<BillingWebhook> getWebhookConfigs(Long organizationId) {
        LambdaQueryWrapper<BillingWebhook> queryWrapper = new LambdaQueryWrapper<>();
        if (organizationId != null) {
            queryWrapper.eq(BillingWebhook::getOrganizationId, organizationId);
        } else {
            queryWrapper.isNull(BillingWebhook::getOrganizationId);
        }
        queryWrapper.eq(BillingWebhook::getIsActive, true);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public Long createWebhookConfig(BillingWebhook webhook) {
        save(webhook);
        return webhook.getId();
    }

    @Override
    @Transactional
    public boolean updateWebhookConfig(BillingWebhook webhook) {
        return updateById(webhook);
    }

    @Override
    @Transactional
    public boolean deleteWebhookConfig(Long webhookId) {
        return removeById(webhookId);
    }

    @Override
    public List<BillingWebhookLog> getWebhookLogs(Long webhookId) {
        LambdaQueryWrapper<BillingWebhookLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BillingWebhookLog::getWebhookId, webhookId)
                   .orderByDesc(BillingWebhookLog::getCreatedAt);
        
        return webhookLogMapper.selectList(queryWrapper);
    }

    /**
     * 发送Webhook
     */
    private void sendWebhook(BillingWebhook webhook, String eventType, Map<String, Object> payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "CoWrite-Webhook/1.0");
            
            // 添加签名
            if (webhook.getSecretKey() != null && !webhook.getSecretKey().isEmpty()) {
                String signature = generateSignature(payloadJson, webhook.getSecretKey());
                headers.set("X-Webhook-Signature", "sha256=" + signature);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                webhook.getWebhookUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // 记录发送日志
            BillingWebhookLog log = new BillingWebhookLog();
            log.setWebhookId(webhook.getId());
            log.setEventType(eventType);
            log.setPayload(payloadJson);
            log.setResponseCode(response.getStatusCodeValue());
            log.setResponseBody(response.getBody());
            log.setStatus(BillingWebhookLog.WebhookStatus.SUCCESS.getCode());
            log.setSentAt(LocalDateTime.now());
            
            webhookLogMapper.insert(log);
            
            logger.info("Webhook发送成功: webhookId={}, eventType={}, responseCode={}", 
                       webhook.getId(), eventType, response.getStatusCodeValue());
            
        } catch (Exception e) {
            logger.error("Webhook发送失败: webhookId={}, eventType={}", webhook.getId(), eventType, e);
            
            // 记录失败日志
            BillingWebhookLog log = new BillingWebhookLog();
            log.setWebhookId(webhook.getId());
            log.setEventType(eventType);
            try {
                log.setPayload(objectMapper.writeValueAsString(payload));
            } catch (Exception ex) {
                log.setPayload("{}");
            }
            log.setStatus(BillingWebhookLog.WebhookStatus.FAILED.getCode());
            log.setRetryCount(0);
            log.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
            
            webhookLogMapper.insert(log);
        }
    }

    /**
     * 检查事件是否被订阅
     */
    private boolean isEventSubscribed(BillingWebhook webhook, String eventType) {
        try {
            List<String> events = objectMapper.readValue(webhook.getEvents(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            return events.contains(eventType) || events.contains("*");
        } catch (Exception e) {
            logger.error("解析Webhook事件配置失败: webhookId={}", webhook.getId(), e);
            return false;
        }
    }

    /**
     * 创建账单载荷
     */
    private Map<String, Object> createInvoicePayload(BillingInvoice invoice) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "invoice");
        payload.put("invoiceId", invoice.getId());
        payload.put("invoiceNumber", invoice.getInvoiceNumber());
        payload.put("userId", invoice.getUserId());
        payload.put("organizationId", invoice.getOrganizationId());
        payload.put("billingPeriod", invoice.getBillingPeriod());
        payload.put("periodStart", invoice.getPeriodStart());
        payload.put("periodEnd", invoice.getPeriodEnd());
        payload.put("totalAmount", invoice.getTotalAmount());
        payload.put("paidAmount", invoice.getPaidAmount());
        payload.put("status", invoice.getStatus());
        payload.put("dueDate", invoice.getDueDate());
        payload.put("timestamp", LocalDateTime.now());
        
        return payload;
    }

    /**
     * 生成Webhook签名
     */
    private String generateSignature(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("生成Webhook签名失败", e);
            return "";
        }
    }
}