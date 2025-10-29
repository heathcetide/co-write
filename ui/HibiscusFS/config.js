const fs = require('fs');
const path = require('path');
const ini = require('ini');

const confPath = path.resolve(__dirname, 'conf/hibiscus.conf');
const raw = fs.readFileSync(confPath, 'utf-8');
const parsed = ini.parse(raw);

module.exports = {
    dbPath: parsed.database.dbPath,
    backupPath: parsed.database.backupPath,
    indexFile: parsed.database.indexFile,

    host: parsed.server.host,
    port: parseInt(parsed.server.port, 10),

    logLevel: parsed.logging.level,
    logDir: parsed.logging.logDir,
    logRotate: parsed.logging.logRotate,
    maxLogFiles: parsed.logging.maxLogFiles,

    upload: {
        tempDir: parsed.upload.tempDir,
        maxSize: parsed.upload.maxSize
    }
};
