import request from '../utils/request';

/**
 * 获取文档列表（分页 + 可选筛选）
 * @param {Object} params - 包含 page, size, knowledgeBaseId, parentId（可选）
 */
export const getDocuments = (params = {}) => {
    return request.get('/documents/list', { params }); // 对应 Node 路由 /api/documents
};

/**
 * 创建新文档
 * @param {Object} data - 包含 title, knowledgeBaseId, parentId, level, order 等字段
 */
export const createDocument = (data) => {
    return request.post('/documents/create', data);
};

/**
 * 更新文档
 * @param {Object} data - 必须包含 id，其他字段根据实际情况传递
 */
export const updateDocument = (data) => {
    return request.post('/documents/update', data);
};

/**
 * 删除文档（逻辑删除）
 * @param {number|string} id - 文档 ID
 */
export const deleteDocument = (id) => {
    return request.delete(`/documents/${id}`);
};

/**
 * 获取当前用户指定知识库下的所有文档
 * @param {number|string} knowledgeBaseId
 */
export const getDocumentsByKnowledgeBase = (knowledgeBaseId) => {
    return request.get('/documents/listByKnowledgeBase', {
        params: { knowledgeBaseId }
    });
};

/**
 * 获取文档的最新内容版本
 * @param {number|string} id - 文档 ID
 */
export const getLatestContent = (id) => {
    return request.get(`/documents/${id}/content`);
};

/**
 * 获取文档的版本列表
 * @param {number|string} id - 文档 ID
 */
export const getVersionList = (id) => {
    return request.get(`/documents/${id}/versions`);
};

/**
 * 获取指定版本内容
 * @param {string} versionId - 版本 ID
 */
export const getVersionById = (versionId) => {
    // 注意：路径中 id 为占位，实际不影响返回结果，可为任意数字
    return request.get(`/documents/0/versions/${versionId}`);
};

/**
 * 保存文档内容（生成新版本）
 * @param {number|string} id - 文档 ID
 * @param {Object} data - 包含 title, content, latestOp 等字段
 */
export const saveContent = (id, data) => {
    return request.post(`/documents/${id}/content`, data);
};