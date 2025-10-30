// index.js - 统一导出
import * as userApi from './user.js';  // 导入用户相关接口
import * as commonApi from './common.js';  // 导入商品相关接口
import * as notificationApi from './notification.js';
import * as operatorApi from './operator.js';
import * as pluginApi from './plugin.js';
import * as knowledgeBaseApi from './knowledgeBase.js';
import * as documentApi from './document.js';
import * as documentShareApi from './documentShare.js';
import * as auditApi from './audit.js';
import * as organizationApi from './organization.js';

// 统一暴露接口
export default {
    userApi,
    commonApi,
    notificationApi,
    operatorApi,
    pluginApi,
    knowledgeBaseApi,
    documentApi,
    documentShareApi,
    auditApi,
    organizationApi
};
