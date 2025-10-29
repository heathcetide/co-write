package com.cowrite.project.netty.server;


import com.cowrite.project.netty.session.SessionManager;
import com.cowrite.project.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    @Resource
    public SessionManager sessionManager;

    @Resource
    public RedisUtils redisUtils;

    //将处理后缓存的文章内容存入数据库
    @Scheduled(fixedRate = 60000) // 每 60 秒执行一次
    public void syncCacheToMongoDB() {
        List<String> docs = sessionManager.getAllCacheDocIds();
        for (String docId : docs) {
            Object content = redisUtils.get("docCache" + docId);
            //TODO
            //将内容放进mongodb（不更新版本）
        }
        sessionManager.clearCacheDocId();
    }

    //判断输入状态
    @Scheduled(fixedRate = 2000)
    public void cleanUpTypingStatus() {
        Set<String> keys = redisUtils.scanKeys("typing_status:*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        for (String key : keys) {
            String[] parts = key.split(":");
            String docId = parts[1];
            String userId = parts[2];
            Long lastActive = (Long) redisUtils.get(key);
            // 判断是否超时（3秒）
            if (now - lastActive > 3000) {
                redisUtils.delete(key);
                sessionManager.sendMessageToOthers(docId,userId,userId+"停止输入");
            }
        }
    }
}
