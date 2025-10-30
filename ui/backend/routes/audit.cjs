const express = require('express');
const router = express.Router();

// 获取文档审计日志
router.get('/document/:documentId', async (req, res) => {
    const { documentId } = req.params;
    const token = req.headers['authorization'];
    try {
        const result = await getDocumentAuditLogs(documentId, token);
        res.status(200).json(result);
    } catch (error) {
        console.error(`Error fetching audit logs for document ${documentId}:`, error.message);
        res.status(500).json({ message: '获取审计日志失败' });
    }
});

// 导出文档审计日志
router.get('/document/:documentId/export', async (req, res) => {
    const { documentId } = req.params;
    const { userId } = req.query;
    const token = req.headers['authorization'];
    try {
        const result = await exportAuditLogs(documentId, userId, token);
        
        // 设置CSV下载响应头
        res.setHeader('Content-Type', 'text/csv; charset=utf-8');
        res.setHeader('Content-Disposition', `attachment; filename="audit_log_${documentId}_${new Date().toISOString().split('T')[0]}.csv"`);
        
        res.status(200).send(result);
    } catch (error) {
        console.error(`Error exporting audit logs for document ${documentId}:`, error.message);
        res.status(500).json({ message: '导出审计日志失败' });
    }
});

// 获取操作日志列表（分页）
router.post('/operation/page', async (req, res) => {
    const token = req.headers['authorization'];
    try {
        const result = await getOperationLogs(req.body, token);
        res.status(200).json(result);
    } catch (error) {
        console.error('Error fetching operation logs:', error.message);
        res.status(500).json({ message: '获取操作日志失败' });
    }
});

module.exports = router;

const axios = require('axios');
const REMOTE_API_BASE_URL = process.env.REMOTE_SERVER || 'http://localhost:8080';

const getHeaders = (token) => ({
    Authorization: token,
    'Content-Type': 'application/json'
});

// 获取文档审计日志
const getDocumentAuditLogs = async (documentId, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/audit/document/${documentId}`, {
        headers: getHeaders(token)
    });
    return response.data;
};

// 导出审计日志
const exportAuditLogs = async (documentId, userId, token) => {
    const response = await axios.get(`${REMOTE_API_BASE_URL}/api/audit/document/${documentId}/export`, {
        params: { userId },
        headers: getHeaders(token),
        responseType: 'arraybuffer' // 用于处理二进制数据
    });
    return response.data;
};

// 获取操作日志
const getOperationLogs = async (data, token) => {
    const response = await axios.post(`${REMOTE_API_BASE_URL}/api/operation_log/page`, data, {
        headers: getHeaders(token)
    });
    return response.data;
};
