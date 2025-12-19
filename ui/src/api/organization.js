import request from '../utils/request';

/**
 * 快速获取当前用户全部参与的组织列表
 * 对应后端 /quickly 接口
 */
export const getOrganizationListQuickly = () => {
    return request.get('/organization/quickly');
};

/**
 * 获取当前用户参与的组织列表
 * 对应后端 /organized 接口（GET方法）
 */
export const getOrganizedOrganizations = () => {
    return request.get('/organization/organized');
};

/**
 * 创建组织邀请码
 * 对应后端 /organization/invite/create 接口
 * @param {Object} data - 包含 organizationId, role, maxUses, expiresAt
 */
export const createInvite = (data) => {
    return request.post('/organization/invite/create', data);
};

/**
 * 创建组织
 * 对应后端 /organization/create 接口
 * @param {Object} data - 包含 name, description, published, maxMembers
 */
export const createOrganization = (data) => {
    return request.post('/organization/create', data);
};

/**
 * 获取指定组织的成员列表
 * 对应后端 /:organizationId/members 接口
 * @param {string} organizationId - 组织ID
 */
export const getOrganizationMembers = (organizationId) => {
    return request.get(`/organization/${organizationId}/members`);
};

/**
 * 切换当前用户的组织
 * 对应后端 /switch 接口
 * @param {string} organizationId - 要切换到的组织ID
 */
export const switchOrganization = (organizationId) => {
    return request.post(`/organizationswitch?organizationId=${organizationId}`);
};

/**
 * 设置组织成员角色
 * 对应后端 /:organizationId/member/:userId/role 接口
 * @param {string} organizationId - 组织ID
 * @param {string} userId - 成员用户ID
 * @param {string} role - 要设置的角色
 */
export const setMemberRole = (organizationId, userId, role) => {
    return request.post(
        `/organization/${organizationId}/member/${userId}/role?role=${role}`
    );
};

/**
 * 移除组织成员
 * 对应后端 /:organizationId/member/:userId 接口
 * @param {string} organizationId - 组织ID
 * @param {string} userId - 要移除的成员用户ID
 */
export const removeOrganizationMember = (organizationId, userId) => {
    return request.delete(`/organization/${organizationId}/member/${userId}`);
};

/**
 * 根据ID获取组织详情
 * 对应后端 /organization/{id} 接口
 * @param {number|string} id - 组织ID
 */
export const getOrganizationById = (id) => {
    return request.get(`/organization/${id}`);
};

/**
 * 更新组织信息
 * 对应后端 /organization/{id} 接口（PUT方法）
 * @param {number|string} id - 组织ID
 * @param {Object} data - 要更新的组织信息
 */
export const updateOrganization = (id, data) => {
    return request.put(`/organization/${id}`, data);
};

/**
 * 删除组织（逻辑删除）
 * 对应后端 /organization/{id} 接口（DELETE方法）
 * @param {number|string} id - 组织ID
 */
export const deleteOrganization = (id) => {
    return request.delete(`/organization/${id}`);
};