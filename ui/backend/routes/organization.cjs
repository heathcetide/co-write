const express = require('express');
const router = express.Router();
const axios = require('axios');

// 远程API基础地址，从环境变量获取或使用默认值
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

/**
 * 构建请求头（通用方法抽离）
 * @param {string} token - 认证令牌
 * @returns {Object} 请求头配置
 */
const buildHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

// ------------------- 路由定义 -------------------
/**
 * 快速获取当前用户全部参与的组织列表
 * 对应Java中的getOrganizationListQuickly方法
 */
router.get('/quickly', async (req, res) => {
    const token = req.headers['authorization'];
    console.log('获取快速组织列表的请求头:', buildHeaders(token));
    try {
        const result = await getOrganizationListQuickly(token);
        res.status(200).json(result);
        console.log('获取快速组织列表成功:', result);
    } catch (error) {
        console.error('获取快速组织列表失败:', error.message);
        res.status(500).json({ message: '获取快速组织列表失败' });
    }
})

/**
 * 获取当前用户参与的组织列表
 * 对应Java中的getOrganizations方法
 */
router.post('/organized', async (req, res) => {
    const token = req.headers['authorization'];
    console.log('获取组织列表的请求头:', buildHeaders(token));
    try {
        const result = await getOrganizedOrganizations(token);
        res.status(200).json(result);
        console.log('获取组织列表成功:', result);
    } catch (error) {
        console.error('获取组织列表失败:', error.message);
        res.status(500).json({ message: '获取组织列表失败' });
    }
});

/**
 * 获取指定组织的成员列表
 * 对应Java中的getOrganizationMembers方法
 */
router.get('/:organizationId/members', async (req, res) => {
    const { organizationId } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await getOrganizationMembers(organizationId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`获取组织${organizationId}成员失败:`, error.message);
        res.status(500).json({ message: '获取组织成员失败' });
    }
});

/**
 * 切换当前用户的组织
 * 对应Java中的switchOrganization方法
 */
router.post('/switch', async (req, res) => {
    const { organizationId } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await switchOrganization(organizationId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`切换组织${organizationId}失败:`, error.message);
        res.status(500).json({ message: '切换组织失败' });
    }
});

/**
 * 设置组织成员角色
 * 对应Java中的setMemberRole方法
 */
router.post('/:organizationId/member/:userId/role', async (req, res) => {
    const { organizationId, userId } = req.params;
    const { role } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await setMemberRole(organizationId, userId, role, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`设置用户${userId}在组织${organizationId}中的角色失败:`, error.message);
        res.status(500).json({ message: '设置成员角色失败' });
    }
});

/**
 * 移除组织成员
 * 对应Java中的removeMember方法
 */
router.delete('/:organizationId/member/:userId', async (req, res) => {
    const { organizationId, userId } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await removeOrganizationMember(organizationId, userId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`从组织${organizationId}中移除用户${userId}失败:`, error.message);
        res.status(500).json({ message: '移除组织成员失败' });
    }
});
/**
 * 创建组织
 * 对应Java中的createOrganization方法
 */
router.post('/create', async (req, res) => {
    const { name, description, published, maxMembers } = req.body;
    const token = req.headers['authorization'];
    console.log("token: ", buildHeaders(token));
    // 参数验证
    if (!name) {
        return res.status(400).json({ message: '组织名称不能为空' });
    }
    let publishedValue = '0';
    if (published === 'yes') {
        publishedValue = '1';
    } else if (published === 'no') {
        publishedValue = '0';
    } else if (published === '1' || published === '0') {
        publishedValue = published;
    }
    const organizationData = {
        name: name,
        description: description || '',
        published: publishedValue,
        maxMembers: maxMembers || null
    };

    try {
        const result = await createOrganization(organizationData, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('创建组织失败:', error.response?.data || error.message);
        // 返回更详细的错误信息
        res.status(500).json({
            message: '创建组织失败',
            error: error.response?.data || error.message,
            status: error.response?.status
        });
    }
});

// ------------------- 方法函数编写 -------------------

/**
 *
 * */
const getOrganizationListQuickly =  async (token) => {
    const response = await axios.get(
        `${REMOTE_API_BASE_URL}/api/organization/quickly`,
        { headers: buildHeaders(token) }
    );
    return response.data;
}


/**
 * 获取当前用户参与的组织列表（对应Java getOrganizations）
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 组织列表数据
 */
const getOrganizedOrganizations = async (token) => {
    const response = await axios.get(
        `${REMOTE_API_BASE_URL}/api/organization/organized`,
        { headers: buildHeaders(token) }
    );
    return response.data;
};

/**
 * 获取指定组织的成员列表（对应Java getOrganizationMembers）
 * @param {string} organizationId - 组织ID
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 成员列表数据
 */
const getOrganizationMembers = async (organizationId, token) => {
    const response = await axios.get(
        `${REMOTE_API_BASE_URL}/api/organization/${organizationId}/members`,
        { headers: buildHeaders(token) }
    );
    return response.data;
};

/**
 * 切换当前用户的组织（对应Java switchOrganization）
 * @param {string} organizationId - 组织ID
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 切换结果
 */
const switchOrganization = async (organizationId, token) => {
    const response = await axios.post(
        `${REMOTE_API_BASE_URL}/api/organization/switch?organizationId=${organizationId}`,
        {},
        { headers: buildHeaders(token) }
    );
    return response.data;
};

/**
 * 设置组织成员角色（对应Java setMemberRole）
 * @param {string} organizationId - 组织ID
 * @param {string} userId - 用户ID
 * @param {string} role - 目标角色
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 设置结果
 */
const setMemberRole = async (organizationId, userId, role, token) => {
    const response = await axios.post(
        `${REMOTE_API_BASE_URL}/api/organization/${organizationId}/member/${userId}/role?role=${role}`,
        {},
        { headers: buildHeaders(token) }
    );
    return response.data;
};

/**
 * 移除组织成员（对应Java removeMember）
 * @param {string} organizationId - 组织ID
 * @param {string} userId - 用户ID
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 移除结果
 */
const removeOrganizationMember = async (organizationId, userId, token) => {
    const response = await axios.delete(
        `${REMOTE_API_BASE_URL}/api/organization/${organizationId}/member/${userId}`,
        { headers: buildHeaders(token) }
    );
    return response.data;
};

module.exports = router;

/**
 * 创建组织（对应Java createOrganization）
 * @param {Object} organizationData - 组织数据
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 创建结果
 */
const createOrganization = async (organizationData, token) => {
    try {
        const response = await axios.post(
            `${REMOTE_API_BASE_URL}/api/organization/create`,
            organizationData,
            {
                headers: buildHeaders(token),
                timeout: 10000 // 添加超时设置
            }
        );
        return response.data;
    } catch (error) {
        console.error('API调用失败:', error.response?.data || error.message);
        throw error; // 重新抛出错误
    }
};