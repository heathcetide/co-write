#!/bin/bash

# 构建项目
mvn clean package -DskipTests

# 停止旧容器
docker stop blog-server
docker rm blog-server

# 启动新容器
docker run -d \
  --name blog-server \
  -p 8080:8080 \
  -v /var/log/blog:/app/logs \
  -e SPRING_PROFILES_ACTIVE=prod \
  cetide/blog-server:latest 