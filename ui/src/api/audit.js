import request from '../utils/request';

/**
 * 获取文档审计日志列表
 * @param {string} documentId - 文档ID
 */
export const getDocumentAuditLogs = (documentId) => {
    return request.get(`/api/audit/document/${documentId}`);
};

/**
 * 导出文档审计日志为CSV
 * @param {string} documentId - 文档ID
 * @param {string} userId - 用户ID
 */
export const exportAuditLogs = (documentId, userId) => {
    return request.get(`/api/audit/document/${documentId}/export`, {
        params: { userId },
        responseType: 'blob'
    });
};

/**
 * 获取操作日志列表（分页）
 * @param {Object} params - 包含 page, size, keyword, sortBy, sortOrder 等
 */
export const getOperationLogs = (params = {}) => {
    return request.post('/api/operation_log/page', params);
};
