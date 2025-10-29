const express = require('express');
const config = require('./config');
const logger = require('./logger');
const HibiscusDB = require('./core/db'); // 使用你写好的核心数据库类
const multer = require('multer'); // 用于上传文件
const fs = require('fs-extra');
const path = require('path');
const errorHandler = require('./middleware/errorHandler');

const app = express();
const db = new HibiscusDB();
db.init()
const upload = multer({ dest: config.upload.tempDir });

app.use(express.json());
app.use(errorHandler)

// 测试接口：状态检查
app.get('/', (req, res) => {
    res.send({
        message: 'HibiscusDB is running!',
        host: config.host,
        port: config.port,
        dbPath: config.dbPath,
        logDir: config.logDir,
        uploadTempDir: config.upload.tempDir
    });
});

// 上传文件
app.post('/files', upload.single('file'), async (req, res) => {
    try {
        const { category, tags } = req.body;
        const content = await fs.readFile(req.file.path, 'utf-8');
        const result = await db.addFile(req.file.originalname, content, category, tags?.split(',') || []);
        res.status(201).json(result);
    } catch (err) {
        logger.error(err.message);
        res.status(500).json({ error: err.message });
    }
});

// 获取文件内容
app.get('/files/:id', async (req, res) => {
    try {
        const content = await db.getFileContent(req.params.id);
        res.send(content);
    } catch (err) {
        res.status(404).json({ error: err.message });
    }
});

// 删除文件
app.delete('/files/:id', async (req, res) => {
    try {
        const deleted = await db.deleteFile(req.params.id);
        res.json(deleted);
    } catch (err) {
        res.status(404).json({ error: err.message });
    }
});

// 搜索
app.get('/search', (req, res) => {
    const { q } = req.query;
    if (!q) return res.status(400).json({ error: 'Query parameter "q" is required.' });
    const results = db.search(q);
    res.json(results);
});

// 获取统计信息
app.get('/stats', (req, res) => {
    const stats = {
        totalFiles: db.index.files.length,
        totalCategories: db.index.categories.length,
        totalTags: db.index.tags.length
    };
    res.json(stats);
});

// 备份
app.post('/backup', async (req, res) => {
    try {
        await db.backup();
        res.json({ message: 'Backup created successfully.' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// 恢复
app.post('/restore/:id', async (req, res) => {
    try {
        await db.restore(req.params.id);
        res.json({ message: `Restored from backup ${req.params.id}` });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// POST /files/:id/blocks/:index
app.post('/files/:id/blocks/:index', async (req, res) => {
    try {
        const { id, index } = req.params;
        const { text } = req.body;
        await db.updateBlock(id, parseInt(index), text);
        res.json({ success: true });
    } catch (e) {
        res.status(500).json({ error: e.message });
    }
});

// 查看历史记录
app.get('/files/:id/history', async (req, res) => {
    try {
        const changes = await db.versionLog.getChangesForFile(req.params.id);
        res.json(changes);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

module.exports = app;
