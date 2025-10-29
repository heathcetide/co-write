const fs = require('fs-extra');
const path = require('path');
const config = require('../config');

class StorageService {
    constructor() {
        this.filesPath = path.join(config.dbPath, 'files');
    }

    async init() {
        await fs.ensureDir(this.filesPath);
    }

    async saveFile(fileName, content) {
        const uniqueName = `${Date.now()}-${fileName}`;
        const filePath = path.join(this.filesPath, uniqueName);
        await fs.writeFile(filePath, content);
        return filePath;
    }

    async readFile(filePath) {
        return await fs.readFile(filePath, 'utf-8');
    }

    async deleteFile(filePath) {
        if (await fs.pathExists(filePath)) {
            await fs.remove(filePath);
        }
    }
}

module.exports = StorageService;
