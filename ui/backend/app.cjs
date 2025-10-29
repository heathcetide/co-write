const express = require('express');
const bodyParser = require('body-parser');
const userRoutes = require('./routes/userRoutes.cjs');
const notificationRoutes = require('./routes/notificationRoutes.cjs');
const operatorRoutes = require('./routes/operator.cjs');
const pluginRoutes = require('./routes/plugin.cjs');
const knowledgeBaseRoutes = require('./routes/knowledgeBase.cjs');
const documentRoutes = require('./routes/document.cjs');
const organizationInviteRoutes = require('./routes/organizationInvite.cjs');
const organizationRoutes = require('./routes/organization.cjs');
const organizationMemberRoutes = require('./routes/organizationMember.cjs');
const requestLogger = require('./middlewares/requestLogger.cjs'); // ✅ 日志中间件

const cors = require('cors');
const authenticate = require('./middlewares/authMiddleware.cjs');
const cronJobs = require('./cronJobs/userStatsCronJob.cjs'); // 启动定时任务

const app = express();

app.use(cors())
app.use(requestLogger);
// 路由配置示例
app.get('/api/info', (req, res) => {
    res.send('PONG');
});

// 中间件
app.use(bodyParser.json());

// 路由
app.use('/api/users', userRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/operator', operatorRoutes);
app.use('/api/plugin', pluginRoutes);
app.use('/api/knowledge-base', knowledgeBaseRoutes)
app.use('/api/documents', documentRoutes);
app.use('/api/organization/invite',organizationInviteRoutes);
app.use('/api/organization', organizationRoutes)
app.use('/api/organization/member', organizationMemberRoutes)

module.exports = app;
