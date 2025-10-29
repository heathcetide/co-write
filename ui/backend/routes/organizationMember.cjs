const express = require('express');
const router = express.Router();
const axios = require('axios');
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

// 构建请求头函数，抽离公共逻辑
const getHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

// ------------------- 方法函数编写 -------------------
/**
 * 新增 OrganizationMember 记录
 * 对应Java中的add方法
 * @param {Object} entity - 要新增的组织成员实体数据
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 新增操作返回的数据
 */
const addOrganizationMember = async (entity, token) => {
    const response = await axios.post(
        `${REMOTE_API_BASE_URL}/api/organization/member`,
        entity,
        { headers: getHeaders(token) }
    );
    return response.data;
};

/**
 * 更新 OrganizationMember 记录
 * 对应Java中的update方法
 * @param {string} id - 要更新的组织成员ID
 * @param {Object} entity - 更新的实体数据
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 更新操作返回的数据
 */
const updateOrganizationMember = async (id, entity, token) => {
    const response = await axios.put(
        `${REMOTE_API_BASE_URL}/api/organization/member/${id}`,
        entity,
        { headers: getHeaders(token) }
    );
    return response.data;
};

/**
 * 删除指定 ID 的 OrganizationMember 记录
 * 对应Java中的delete方法
 * @param {string} id - 要删除的组织成员ID
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 删除操作返回的数据
 */
const deleteOrganizationMember = async (id, token) => {
    const response = await axios.delete(
        `${REMOTE_API_BASE_URL}/api/organization/member/${id}`,
        { headers: getHeaders(token) }
    );
    return response.data;
};

/**
 * 根据 ID 获取 OrganizationMember 详情
 * 对应Java中的getById方法
 * @param {string} id - 要获取详情的组织成员ID
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 组织成员详情数据
 */
const getOrganizationMemberById = async (id, token) => {
    const response = await axios.get(
        `${REMOTE_API_BASE_URL}/api/organization/member/${id}`,
        { headers: getHeaders(token) }
    );
    return response.data;
};

/**
 * 获取所有 OrganizationMember 列表（不分页）
 * 对应Java中的list方法
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 组织成员列表数据
 */
const listOrganizationMembers = async (token) => {
    const response = await axios.get(
        `${REMOTE_API_BASE_URL}/api/organization/member`,
        { headers: getHeaders(token) }
    );
    return response.data;
};

/**
 * 分页查询 OrganizationMember 列表
 * 对应Java中的getPage方法
 * @param {Object} pageRequest - 分页请求参数
 * @param {string} token - 认证令牌
 * @returns {Promise<any>} 分页查询结果数据
 */
const pageOrganizationMembers = async (pageRequest, token) => {
    const response = await axios.post(
        `${REMOTE_API_BASE_URL}/api/organization/member/page`,
        pageRequest,
        { headers: getHeaders(token) }
    );
    return response.data;
};

// ------------------- 路由定义 -------------------
/**
 * 新增 OrganizationMember 记录
 */
router.post('/', async (req, res) => {
    const token = req.headers['authorization'];
    const entity = req.body;
    try {
        const result = await addOrganizationMember(entity, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('新增组织成员失败:', error.message);
        res.status(500).json({ message: '新增组织成员失败' });
    }
});

/**
 * 更新 OrganizationMember 记录
 */
router.put('/:id', async (req, res) => {
    const { id } = req.params;
    const token = req.headers['authorization'];
    const entity = req.body;
    try {
        const result = await updateOrganizationMember(id, entity, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`更新组织成员${id}失败:`, error.message);
        res.status(500).json({ message: '更新组织成员失败' });
    }
});

/**
 * 删除指定 ID 的 OrganizationMember 记录
 */
router.delete('/:id', async (req, res) => {
    const { id } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await deleteOrganizationMember(id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`删除组织成员${id}失败:`, error.message);
        res.status(500).json({ message: '删除组织成员失败' });
    }
});

/**
 * 根据 ID 获取 OrganizationMember 详情
 */
router.get('/:id', async (req, res) => {
    const { id } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await getOrganizationMemberById(id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`获取组织成员${id}详情失败:`, error.message);
        res.status(500).json({ message: '获取组织成员详情失败' });
    }
});

/**
 * 获取所有 OrganizationMember 列表（不分页）
 */
router.get('/', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await listOrganizationMembers(token);
        res.status(200).json(result);
    } catch (error) {
        console.error('获取组织成员列表失败:', error.message);
        res.status(500).json({ message: '获取组织成员列表失败' });
    }
});

/**
 * 分页查询 OrganizationMember 列表
 */
router.post('/page', async (req, res) => {
    const token = req.headers['authorization'];
    const pageRequest = req.body;
    try {
        const result = await pageOrganizationMembers(pageRequest, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('分页查询组织成员失败:', error.message);
        res.status(500).json({ message: '分页查询组织成员失败' });
    }
});

module.exports = router;