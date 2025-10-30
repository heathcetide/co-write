package com.cowrite.project.service;

/**
 * 计费事件发布器接口
 *
 * @author heathcetide
 */
public interface BillingEventPublisher {

    /**
     * 发布文档编辑事件
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param operation  操作类型
     * @param metadata   元数据
     */
    void publishDocumentEditEvent(Long userId, String documentId, String operation, Object metadata);

    /**
     * 发布AI调用事件
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param tokens     消耗的Token数
     * @param model      使用的模型
     */
    void publishAiCallEvent(Long userId, String documentId, Integer tokens, String model);

    /**
     * 发布协作事件
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param operation  操作类型
     * @param metadata   元数据
     */
    void publishCollaborationEvent(Long userId, String documentId, String operation, Object metadata);

    /**
     * 发布存储使用事件
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param bytes      使用的字节数
     * @param operation  操作类型（UPLOAD、DELETE等）
     */
    void publishStorageUsageEvent(Long userId, String documentId, Long bytes, String operation);

    /**
     * 发布用户登录事件
     *
     * @param userId 用户ID
     * @param ip     登录IP
     */
    void publishUserLoginEvent(Long userId, String ip);

    /**
     * 发布用户注册事件
     *
     * @param userId 用户ID
     * @param source 注册来源
     */
    void publishUserRegisterEvent(Long userId, String source);
}
