const express = require('express');
const router = express.Router();

// 获取文档列表（分页 + 可选筛选）
router.get('/list', async (req, res) => {
    console.log(req.query);
    const { page = 1, size = 20, knowledgeBaseId, parentId } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await fetchDocuments(page, size, knowledgeBaseId, parentId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error fetching document list:', error.message);
        res.status(500).json({ message: '获取文档列表失败' });
    }
});

// 创建文档
router.post('/create', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await createDocument(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error creating document:', error.message);
        res.status(500).json({ message: '创建文档失败' });
    }
});

// 更新文档
router.post('/update', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await updateDocument(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error updating document:', error.message);
        res.status(500).json({ message: '更新文档失败' });
    }
});

// 删除文档
router.delete('/:id', async (req, res) => {
    const { id } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await deleteDocument(id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error deleting document ${id}:`, error.message);
        res.status(500).json({ message: '删除文档失败' });
    }
});

// 获取指定知识库下文档列表（当前用户）
router.get('/listByKnowledgeBase', async (req, res) => {
    const { knowledgeBaseId } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await listByKnowledgeBase(knowledgeBaseId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error fetching documents by knowledgeBase:', error.message);
        res.status(500).json({ message: '获取文档失败' });
    }
});

// 获取最新文档内容
router.get('/:id/content', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await getLatestContent(req.params.id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error fetching latest content of document ${req.params.id}:`, error.message);
        res.status(500).json({ message: '获取文档内容失败' });
    }
});

// 获取文档版本列表
router.get('/:id/versions', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await getVersionList(req.params.id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error fetching versions for document ${req.params.id}:`, error.message);
        res.status(500).json({ message: '获取版本列表失败' });
    }
});

// 获取指定版本内容
router.get('/:id/versions/:versionId', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await getVersionById(req.params.versionId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error fetching version ${req.params.versionId}:`, error.message);
        res.status(500).json({ message: '获取版本内容失败' });
    }
});

// 保存文档内容（新增版本）
router.post('/:id/content', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await saveContent(req.params.id, req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error saving content for document ${req.params.id}:`, error.message);
        res.status(500).json({ message: '保存文档失败' });
    }
});

module.exports = router;

const axios = require('axios');
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

const getHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

// 获取文档列表
const fetchDocuments = async (page, size, knowledgeBaseId, parentId, token) => {
    const params = new URLSearchParams({ page, size });
    if (knowledgeBaseId) params.append('knowledgeBaseId', knowledgeBaseId);
    if (parentId) params.append('parentId', parentId);

    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document/list?${params}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 创建文档
const createDocument = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document/create`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 更新文档
const updateDocument = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document/update`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 删除文档（逻辑删除）
const deleteDocument = async (id, token) => {
    const response = await axios.delete(`${REMOTE_API_BASE_URL}/api/document/delete/${id}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

const listByKnowledgeBase = async (knowledgeBaseId, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document/listByKnowledgeBase?knowledgeBaseId=${knowledgeBaseId}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

const getLatestContent = async (id, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document/${id}/content`, {
        headers: getHeaders(token)
    });
    return response.data;
};

const getVersionList = async (id, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document/${id}/versions`, {
        headers: getHeaders(token)
    });
    return response.data;
};

const getVersionById = async (versionId, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document/0/versions/${versionId}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

const saveContent = async (id, data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document/${id}/content`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};