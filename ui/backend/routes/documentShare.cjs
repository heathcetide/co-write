const express = require('express');
const router = express.Router();

// 创建文档分享链接
router.post('/create', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await createShareLink(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error creating share link:', error.message);
        res.status(500).json({ message: '创建分享链接失败' });
    }
});

// 验证分享链接口令
router.post('/validate-password', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await validatePassword(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error validating password:', error.message);
        res.status(400).json({ message: error.message || '口令验证失败' });
    }
});

// 获取分享链接信息
router.get('/:code', async (req, res) => {
    const { code } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await getShareInfo(code, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error fetching share info for code ${code}:`, error.message);
        res.status(500).json({ message: '获取分享信息失败' });
    }
});

// 获取文档权限列表
router.get('/permissions', async (req, res) => {
    const { documentId } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await getDocumentPermissions(documentId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error fetching document permissions:', error.message);
        res.status(500).json({ message: '获取文档权限失败' });
    }
});

// 设置文档权限
router.post('/permissions', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await setDocumentPermission(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error setting document permission:', error.message);
        res.status(500).json({ message: '设置文档权限失败' });
    }
});

// 更新文档权限
router.put('/permissions', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await updateDocumentPermission(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error updating document permission:', error.message);
        res.status(500).json({ message: '更新文档权限失败' });
    }
});

// 删除文档权限
router.delete('/permissions/:id', async (req, res) => {
    const { id } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await deleteDocumentPermission(id, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error deleting document permission ${id}:`, error.message);
        res.status(500).json({ message: '删除文档权限失败' });
    }
});

module.exports = router;

const axios = require('axios');
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

const getHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

// 创建分享链接
const createShareLink = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document-share/create`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 验证分享口令
const validatePassword = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document-share/validate-password`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 获取分享信息
const getShareInfo = async (code, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document-share/${code}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 获取文档权限
const getDocumentPermissions = async (documentId, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/document_permission`, {
        params: { documentId },
        headers: getHeaders(token)
    });
    return response.data;
};

// 设置文档权限
const setDocumentPermission = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/document_permission`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 更新文档权限
const updateDocumentPermission = async (data, token) => {
    const response = await axios.put(`${REMOTE_API_BASE_URL}/api/document_permission`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 删除文档权限
const deleteDocumentPermission = async (id, token) => {
    const response = await axios.delete(`${REMOTE_API_BASE_URL}/api/document_permission/${id}`, {
        headers: getHeaders(token)
    });
    return response.data;
};
