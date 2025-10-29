const fs = require('fs-extra');
const path = require('path');

const StorageService = require('./storage');
const logger = require('../logger');
const config = require('../config');

class HibiscusDB {
  constructor() {
    this.storageService = new StorageService();
    this.storageService.init();
    this.indexPath = path.join(config.dbPath, 'index.json');
    this.index = { files: [], categories: [], tags: [] };
    this.init();
  }

  async init() {
    try {
      if (!(await fs.pathExists(this.indexPath))) {
        await fs.writeJson(this.indexPath, {
          ...this.index,
        });
      }
      const data = await fs.readJson(this.indexPath);
      this.index = {
        files: data.files || [],
        categories: data.categories || [],
        tags: data.tags || [],
      };
    } catch (error) {
      logger.error(`Error initializing database: ${error.message}`);
      throw error;
    }
  }

  async addFile(fileName, content, category, tags = []) {
    const start = Date.now();
    try {
      const fileId = Date.now().toString();
      const filePathInDB = await this.storageService.saveFile(fileName, content);

      const fileData = {
        id: fileId,
        fileName,
        filePath: filePathInDB,
        category,
        tags,
      };

      this.index.files.push(fileData);
      this.index.categories = [...new Set([...this.index.categories, category])];
      this.index.tags = [...new Set([...this.index.tags, ...tags])];

      await fs.writeJson(this.indexPath, {
        ...this.index,
      });

      logger.info(`File added: ${fileData.fileName} (took ${Date.now() - start} ms)`);
      return fileData;
    } catch (error) {
      logger.error(`Error adding file: ${error.message}`);
      throw error;
    }
  }

  search(query) {
    const matched = this.index.files.filter(file =>
        file.fileName.includes(query)
    );
    return Promise.resolve(matched);
  }

  getFilesByCategory(category) {
    return this.index.files.filter((file) => file.category === category);
  }

  getFilesByTag(tag) {
    return this.index.files.filter((file) => file.tags.includes(tag));
  }

  async getFileContent(fileId) {
    const file = this.index.files.find((f) => f.id === fileId);
    if (!file) throw new Error('File not found');
    return await this.storageService.readFile(file.filePath);
  }

  async deleteFile(fileId) {
    try {
      const index = this.index.files.findIndex(f => f.id === fileId);
      if (index === -1) throw new Error('File not found');

      const file = this.index.files[index];
      await this.storageService.deleteFile(file.filePath);
      this.index.files.splice(index, 1);
      this._refreshCategoriesAndTags();
      await fs.writeJson(this.indexPath, {
        ...this.index,
      });

      logger.info(`File deleted: ${file.fileName}`);
      return file;
    } catch (error) {
      logger.error(`Error deleting file: ${error.message}`);
      throw error;
    }
  }

  async backup() {
    try {
      const backupId = Date.now().toString();
      const backupDir = path.join(config.backupPath, backupId);
      if (!(await fs.pathExists(config.backupPath))) {
        await fs.mkdir(config.backupPath);
      }
      await fs.mkdir(backupDir);
      await fs.copy(config.dbPath, backupDir);
      logger.info(`Backup created at ${backupDir}`);
    } catch (error) {
      logger.error(`Error creating backup: ${error.message}`);
      throw error;
    }
  }

  async restore(backupId) {
    try {
      const backupDir = path.join(config.backupPath, backupId);
      if (!(await fs.pathExists(backupDir))) {
        throw new Error(`Backup directory ${backupDir} does not exist`);
      }

      await fs.copy(backupDir, config.dbPath);
      const data = await fs.readJson(this.indexPath);

      this.index = {
        files: data.files || [],
        categories: data.categories || [],
        tags: data.tags || [],
      };
      logger.info(`Data restored from ${backupDir}`);
    } catch (error) {
      logger.error(`Error restoring data: ${error.message}`);
      throw error;
    }
  }

  _refreshCategoriesAndTags() {
    const categories = new Set();
    const tags = new Set();

    for (const file of this.index.files) {
      categories.add(file.category);
      for (const tag of file.tags) tags.add(tag);
    }

    this.index.categories = Array.from(categories);
    this.index.tags = Array.from(tags);
  }
}

module.exports = HibiscusDB;
