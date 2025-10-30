import request from '../utils/request';

/**
 * 创建文档分享链接
 * @param {Object} data - 包含 documentId, accessPassword, permission, shareType, remark, expireTime 等
 */
export const createShareLink = (data) => {
    return request.post('/api/document-share/create', data);
};

/**
 * 验证分享链接口令
 * @param {Object} data - 包含 code, password
 */
export const validatePassword = (data) => {
    return request.post('/api/document-share/validate-password', data);
};

/**
 * 获取分享链接信息
 * @param {string} code - 分享码
 */
export const getShareInfo = (code) => {
    return request.get(`/api/document-share/${code}`);
};

/**
 * 获取文档权限列表
 * @param {string} documentId - 文档ID
 */
export const getDocumentPermissions = (documentId) => {
    return request.get('/api/document_permission', {
        params: { documentId }
    });
};

/**
 * 设置文档权限
 * @param {Object} data - 包含 documentId, userId, permission, disableExport 等
 */
export const setDocumentPermission = (data) => {
    return request.post('/api/document_permission', data);
};

/**
 * 更新文档权限
 * @param {Object} data - 包含 id, permission, disableExport 等
 */
export const updateDocumentPermission = (data) => {
    return request.put('/api/document_permission', data);
};

/**
 * 删除文档权限
 * @param {number} id - 权限记录ID
 */
export const deleteDocumentPermission = (id) => {
    return request.delete(`/api/document_permission/${id}`);
};
