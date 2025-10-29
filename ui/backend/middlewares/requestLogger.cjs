const fs = require('fs');
const path = require('path');

const logFilePath = path.resolve(__dirname, '../logs/request.log');

// 确保 logs 文件夹存在
if (!fs.existsSync(path.dirname(logFilePath))) {
    fs.mkdirSync(path.dirname(logFilePath), { recursive: true });
}

const requestLogger = (req, res, next) => {
    const timestamp = new Date().toISOString();
    const logEntry = {
        time: timestamp,
        method: req.method,
        url: req.originalUrl,
        headers: req.headers,
        body: req.body,
        query: req.query,
        ip: req.ip,
    };

    const logText = `[${timestamp}] ${req.method} ${req.originalUrl}\n` +
        `IP: ${req.ip}\n` +
        `Headers: ${JSON.stringify(req.headers)}\n` +
        `Query: ${JSON.stringify(req.query)}\n` +
        `Body: ${JSON.stringify(req.body)}\n\n`;

    fs.appendFile(logFilePath, logText, (err) => {
        if (err) {
            console.error('❌ 请求日志写入失败:', err);
        }
    });

    next(); // 继续处理请求
};

module.exports = requestLogger;
