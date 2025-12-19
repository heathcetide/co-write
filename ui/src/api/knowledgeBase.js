import request from '../utils/request';

/**
 * 获取当前用户的个人知识库列表
 */
export const getPersonalKnowledgeBases = () => {
    return request.get('/knowledge-base/getPersonal');
};

/**
 * 创建个人知识库
 * @param {Object} data - 知识库对象，如 { name, description, coverUrl }
 */
export const createPersonalKnowledgeBase = (data) => {
    return request.post('/knowledge-base/createPersonal', data);
};

/**
 * 更新知识库信息
 * @param {Object} data - 要更新的知识库对象（需要包含 id）
 */
export const updateKnowledgeBase = (data) => {
    return request.post('/knowledge-base/update', data);
};

/**
* 获取当前组织的知识库列表
 * @param {number|string}  organizationId - 组织 ID
*/
export const getOrganizationKnowledgeBases = (organizationId) => {
    return request.get(`/knowledge-base/getOrganizationKnowledgeBases/${organizationId}`);
};

/**
 * 根据ID获取知识库详情
 * @param {number|string} id - 知识库ID
 */
export const getKnowledgeBaseById = (id) => {
    return request.get(`/knowledge-base/${id}`);
};

/**
 * 删除知识库
 * @param {number|string} id - 知识库ID
 */
export const deleteKnowledgeBase = (id) => {
    return request.delete(`/knowledge-base/${id}`);
};
