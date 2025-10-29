package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import com.cowrite.project.netty.session.SessionManager;
import com.cowrite.project.utils.RedisUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContentHandler {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private SessionManager sessionManager;

    @Resource
    private MongoTemplate mongoTemplate;

    //记录文档的历史版本
    private HashMap<String, Map<String, StringBuilder>> docVersion = new HashMap<>();
    //储存文档缓存内容
    private HashMap<String, StringBuilder> cacheContent = new HashMap<>();
    //文档的操作次数
    private HashMap<String, Integer> operationNum = new HashMap<>();

    public void applyOperation(String docId, NettyMessage nettyMessage) {
        StringBuilder docContent = cacheContent.computeIfAbsent(docId, k -> new StringBuilder());


        if (MessageType.CONTENT_INSERT.equals(nettyMessage.getOperationType())) {
            int pos = nettyMessage.getPos();
            String content = nettyMessage.getContent();
            if (pos >= 0 && pos <= docContent.length()) {
                docContent.insert(pos, content);
            }
        } else if (MessageType.CONTENT_DELETE.equals(nettyMessage.getOperationType())) {
            int pos = nettyMessage.getPos();
            int deleteLength = nettyMessage.getLength();
            int end = Math.min(pos + deleteLength, docContent.length());
            if (pos >= 0 && pos <= end) {
                docContent.delete(pos, end);
            }
        }
        System.out.println("到这了");
        System.out.println(docContent);

        // 更新操作次数
        int newCount = operationNum.compute(docId, (k, v) -> v == null ? 1 : v + 1);

        // 每100次持久化
        if (newCount % 100 == 0) {
            contentCache(docId, docContent);
        }
    }

    //撤回
    public void undoContent(String docId, NettyMessage nettyMessage) {
        StringBuilder docContent = cacheContent.get(docId);
        // 文档内容不存在，无法撤回
        if (docContent == null) {
            return;
        }

        // 检查操作类型并执行对应的逆向操作
        if (MessageType.CONTENT_INSERT.equals(nettyMessage.getOperationType())) {
            // 原操作是 INSERT -> 撤回操作是 DELETE
            int pos = nettyMessage.getPos();
            String content = nettyMessage.getContent();
            int deleteLength = (content == null) ? 0 : content.length();
            int end = pos + deleteLength;
            // 复用 applyOperation 中删除操作的边界检查逻辑
            if (pos >= 0 && pos <= docContent.length() && end > pos) { // 确保 pos 有效且要删除的范围有内容
                int actualEnd = Math.min(end, docContent.length()); // 确保 end 不越界
                docContent.delete(pos, actualEnd);
            }
            // 如果 pos 无效或要删除的范围为空，则无需操作
        } else if (MessageType.CONTENT_DELETE.equals(nettyMessage.getOperationType())) {
            // 原操作是 DELETE -> 撤回操作是 INSERT
            int pos = nettyMessage.getPos();
            String content = nettyMessage.getContent(); // 被删除的内容，用于恢复

            // 复用 applyOperation 中插入操作的边界检查逻辑
            if (pos >= 0 && pos <= docContent.length()) {
                // 直接插入被删除的内容
                docContent.insert(pos, content == null ? "" : content); // 处理 content 为 null 的情况
            }
            // 如果 pos 无效，则无法插入，不进行操作
        }
    }

    //放入redis用于定时任务
    public void contentCache(String docId, Object content) {
        if (!sessionManager.getAllCacheDocIds().contains(docId)) {
            sessionManager.putCacheDocId(docId);
        }
        redisUtils.set("docCache" + docId, content);
    }

    //拿到最新的缓存内容
    public String getCacheContent(String docID) {
        return cacheContent.get(docID).toString();
    }

    //存入版本对应的内容
    public void updateVersion(String docId) {
        String content = getCacheContent(docId);
        /*
        TODO
        版本调整
         */
        docVersion.computeIfAbsent(docId, k -> new HashMap<>())
                .put("新的版本号", cacheContent.get(docId));
    }

    //获取指定版本的内容
    public String getVersionContent(String docId, String version) {
        StringBuilder content = docVersion.get(docId).get(version);
        return content.toString();
    }

}