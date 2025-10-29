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
 * 对应后端 /organized 接口
 */
export const getOrganizedOrganizations = () => {
    return request.post('/organization/organized');
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