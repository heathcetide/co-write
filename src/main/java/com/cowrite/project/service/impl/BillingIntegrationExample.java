package com.cowrite.project.service.impl;

import com.cowrite.project.service.BillingEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 计费系统集成示例
 * 展示如何在现有业务代码中集成计费事件
 *
 * @author heathcetide
 */
@Service
public class BillingIntegrationExample {

    @Autowired
    private BillingEventPublisher billingEventPublisher;

    /**
     * 示例：在文档编辑时发布计费事件
     */
    public void onDocumentEdit(Long userId, String documentId, String operation, Object metadata) {
        // 发布文档编辑事件
        billingEventPublisher.publishDocumentEditEvent(userId, documentId, operation, metadata);
    }

    /**
     * 示例：在AI调用时发布计费事件
     */
    public void onAiCall(Long userId, String documentId, Integer tokens, String model) {
        // 发布AI调用事件
        billingEventPublisher.publishAiCallEvent(userId, documentId, tokens, model);
    }

    /**
     * 示例：在协作操作时发布计费事件
     */
    public void onCollaboration(Long userId, String documentId, String operation, Object metadata) {
        // 发布协作事件
        billingEventPublisher.publishCollaborationEvent(userId, documentId, operation, metadata);
    }

    /**
     * 示例：在文件上传/删除时发布存储使用事件
     */
    public void onStorageOperation(Long userId, String documentId, Long bytes, String operation) {
        // 发布存储使用事件
        billingEventPublisher.publishStorageUsageEvent(userId, documentId, bytes, operation);
    }

    /**
     * 示例：在用户登录时发布事件
     */
    public void onUserLogin(Long userId, String ip) {
        // 发布用户登录事件
        billingEventPublisher.publishUserLoginEvent(userId, ip);
    }

    /**
     * 示例：在用户注册时发布事件
     */
    public void onUserRegister(Long userId, String source) {
        // 发布用户注册事件
        billingEventPublisher.publishUserRegisterEvent(userId, source);
    }
}
