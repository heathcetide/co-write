#!/bin/bash

# 当前运行版本（蓝）
CURRENT_VERSION="blue"

# 确定新版本（绿）
NEW_VERSION="green"

# 构建阶段
if ! docker build -t blog-server:$NEW_VERSION . ; then
    sendAlert "构建失败"
    exit 1
fi

# 启动新版本
if ! docker run -d --name blog-$NEW_VERSION -p 8081:8080 blog-server:$NEW_VERSION; then
    sendAlert "容器启动失败"
    exit 2
fi

# 健康检查（最多等待2分钟）
for i in {1..12}; do
    STATUS=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8081/actuator/health)
    if [ "$STATUS" -eq 200 ]; then
        break
    fi
    sleep 10
done

if [ "$STATUS" -ne 200 ]; then
    docker logs blog-$NEW_VERSION > deploy-error.log
    sendAlert "健康检查失败" deploy-error.log
    exit 3
fi

# 切换流量
if ! sed -i.bak "s/blog-$CURRENT_VERSION/blog-$NEW_VERSION/" nginx.conf && nginx -s reload; then
    cp nginx.conf.bak nginx.conf
    sendAlert "流量切换失败，已回退配置"
    exit 4
fi

# 清理旧版本
docker stop blog-$CURRENT_VERSION && docker rm blog-$CURRENT_VERSION || true

sendNotification "部署成功，当前版本：$NEW_VERSION" 