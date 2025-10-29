const express = require('express');
const router = express.Router();

/**
 * 获取当前组织的知识库:
 * */
router.get('/getOrganizationKnowledgeBases/:id', async (req, res) => {
    const token = req.headers['authorization'];
    const organizationId = req.params.id;
    try {
        const data = await fetchOrganizationKnowledgeBases(token, organizationId);
        res.status(200).json(data);
    } catch (error) {
        console.error('Error in GET /knowledge-base/getOrganizationKnowledgeBases:', error.message);
        if (error.response?.status === 401) {
            return res.status(401).json({ message: '未授权：令牌无效或已过期' });
        }
        res.status(500).json({ message: '获取知识库失败' });
    }
})


// 获取当前用户的个人知识库列表
router.get('/getPersonal', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const data = await fetchPersonalKnowledgeBases(token);
        res.status(200).json(data);
    } catch (error) {
        console.error('Error in GET /knowledge-base/getPersonal:', error.message);
        if (error.response?.status === 401) {
            return res.status(401).json({ message: '未授权：令牌无效或已过期' });
        }
        res.status(500).json({ message: '获取知识库失败' });
    }
});

// 创建个人知识库
router.post('/createPersonal', async (req, res) => {
    const token = req.headers['authorization'];
    const knowledgeBase = req.body;
    try {
        const data = await createPersonalKnowledgeBase(knowledgeBase, token);
        res.status(200).json(data);
    } catch (error) {
        console.error('Error in POST /knowledge-base/createPersonal:', error.message);
        if (error.response?.status === 401) {
            return res.status(401).json({ message: '未授权：令牌无效或已过期' });
        }
        res.status(500).json({ message: '创建知识库失败' });
    }
});

// 更新知识库
router.post('/update', async (req, res) => {
    const token = req.headers['authorization'];
    const knowledgeBase = req.body;
    try {
        const data = await updateKnowledgeBase(knowledgeBase, token);
        res.status(200).json(data);
    } catch (error) {
        console.error('Error in POST /knowledge-base/update:', error.message);
        if (error.response?.status === 401) {
            return res.status(401).json({ message: '未授权：令牌无效或已过期' });
        }
        res.status(500).json({ message: '更新知识库失败' });
    }
});

module.exports = router;

const axios = require('axios');
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

const getHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

/**
 * 获取当前组织的知识库方法:
 * */
const fetchOrganizationKnowledgeBases = async (token, organizationId) => {
    console.log('id的值再实现方法中为：', organizationId);
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/knowledge-base/getOrganizationKnowledgeBases/${organizationId}`, {
        headers: getHeaders(token)
    });
    return response.data;
};


// 获取当前用户的个人知识库列表
const fetchPersonalKnowledgeBases = async (token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/knowledge-base/getPersonal`, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 创建个人知识库
const createPersonalKnowledgeBase = async (knowledgeBase, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/knowledge-base/createPersonal`, knowledgeBase, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 更新知识库
const updateKnowledgeBase = async (knowledgeBase, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/knowledge-base/update`, knowledgeBase, {
        headers: getHeaders(token)
    });
    return response.data;
};
