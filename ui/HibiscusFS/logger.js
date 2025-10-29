const winston = require('winston');
const DailyRotateFile = require('winston-daily-rotate-file');
const config = require('./config');
const path = require('path');

// 日志目录确保存在
const logDir = path.join(__dirname, 'logs');

// 按日期进行分类的日志传输器
const dailyRotateTransport = new DailyRotateFile({
    filename: 'hibiscusDB-%DATE%.log',         // 生成 hibiscusDB-2025-08-03.log 类似文件
    dirname: logDir,                            // 日志目录
    datePattern: 'YYYY-MM-DD',                 // 按天分类（也支持按小时、分钟）
    zippedArchive: false,                      // 是否压缩旧日志文件
    maxSize: '10m',                             // 每个日志最大大小（可选）
    maxFiles: '14d',                            // 最多保留 14 天（可选）
    level: config.logLevel,                     // 日志级别
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.printf(({ timestamp, level, message }) => {
            return `${timestamp} [${level.toUpperCase()}]: ${message}`;
        })
    ),
});

// 创建 logger
const logger = winston.createLogger({
    transports: [dailyRotateTransport],
});

// 如果是开发环境，同时在控制台输出日志
if (process.env.NODE_ENV !== 'production') {
    logger.add(
        new winston.transports.Console({
            format: winston.format.simple(),
        })
    );
}

module.exports = logger;
