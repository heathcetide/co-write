package com.cowrite.project.netty.session;


import com.cowrite.project.netty.protocol.MessageResponse;
import com.cowrite.project.netty.protocol.NettyMessage;
import com.cowrite.project.utils.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session管理器
 */
@Component
public class SessionManager {
    //绑定channel和user
    public static final AttributeKey<String> USER_ID = AttributeKey.valueOf("userId");
    //绑定channel和docId
    public static final AttributeKey<String> DOC_ID = AttributeKey.valueOf("docId");
    //用来记录有哪些文档有缓存
    private List<String> cacheDoc = Collections.synchronizedList(new ArrayList<>());

    private Map<String, Map<String, UserSession>> sessions = new ConcurrentHashMap<>();

    //新增在线用户
    public void addSessions(String docId,String userId,Channel channel){
        sessions.computeIfAbsent(docId, k -> new HashMap<>())
                .put(userId, new UserSession(userId, docId, channel));
    }

    //删除channel
    public  void deleteSessions(String docId,String userId) {
        if (docId == null || userId == null) {
            return; // 防止 null key
        }

        sessions.computeIfPresent(docId, (key, userSessions) -> {
            userSessions.remove(userId);

            // 如果当前文档已无用户，可以移除该文档的 map，节省内存
            if (userSessions.isEmpty()) {
                return null; // remove the entry
            }

            return userSessions;
        });
    }


    //给指定用户发消息·
    public void sendMessageToUser(String docId, String userId, MessageResponse messageResponse) {
        // 参数检查
        if (docId == null || userId == null || messageResponse == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }

        UserSession session = userSessions.get(userId);
        if (session != null) {
            Channel channel = session.getChannel();
            if (channel != null && channel.isActive()) {
                try {
                    // 使用 MessageResponse 的 toJson() 方法序列化为 JSON
                    String json = messageResponse.toJson();
                    // 包装成 WebSocket 文本帧并发送
                    channel.writeAndFlush(new TextWebSocketFrame(json));
                } catch (Exception e) {
                    // 防止 JSON 序列化失败导致异常中断（例如 payload 中有不可序列化对象）
                    e.printStackTrace(); // 建议使用日志框架：log.error("消息序列化失败", e);
                }
            }
        }
    }


    //查找用户channel
    public UserSession findSessions(String docId,String userId){
        return sessions.get(docId).get(userId);
    }

    //查找编辑文档的所有用户
    public Set<String> getOnlineUsers(String docId) {
        if (docId == null) {
            return Collections.emptySet();
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null) {
            return Collections.emptySet();
        }

        // 返回当前文档中所有用户的 ID 集合（不可变）
        return Collections.unmodifiableSet(userSessions.keySet());
    }

    //同步信息
    public void sendMessageToOthers(String docId, String excludeUserId, MessageResponse messageResponse) {
        if (docId == null || excludeUserId == null || messageResponse == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }

        String json = messageResponse.toJson();
        // 创建一个引用计数为 1 的帧
        TextWebSocketFrame frame = new TextWebSocketFrame(Unpooled.copiedBuffer(json, StandardCharsets.UTF_8));

        // 发送给其他人
        for (Map.Entry<String, UserSession> entry : userSessions.entrySet()) {
            String userId = entry.getKey();
            UserSession session = entry.getValue();

            if (!userId.equals(excludeUserId)) {
                Channel channel = session.getChannel();
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(frame.retainedDuplicate()); // retainedDuplicate = retain + copy
                }
            }
        }

        // 释放原始帧
        frame.release();
    }

    //同步信息
    public void sendMessageToOthers(String docId, String excludeUserId, NettyMessage nettyMessage) {
        if (docId == null || excludeUserId == null || nettyMessage == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }

        String json = nettyMessage.toJson();
        // 创建一个引用计数为 1 的帧
        TextWebSocketFrame frame = new TextWebSocketFrame(Unpooled.copiedBuffer(json, StandardCharsets.UTF_8));

        // 发送给其他人
        for (Map.Entry<String, UserSession> entry : userSessions.entrySet()) {
            String userId = entry.getKey();
            UserSession session = entry.getValue();

            if (!userId.equals(excludeUserId)) {
                Channel channel = session.getChannel();
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(frame.retainedDuplicate()); // retainedDuplicate = retain + copy
                }
            }
        }

        // 释放原始帧
        frame.release();
    }

    //同步信息
    public void sendMessageToOthers(String docId, String excludeUserId, String content) {
        // 参数检查
        if (docId == null || excludeUserId == null || content == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return; // 没有用户在线
        }

        // 创建一个引用计数为 1 的帧
        TextWebSocketFrame frame = new TextWebSocketFrame(Unpooled.copiedBuffer(content, StandardCharsets.UTF_8));

        try {
            // 发送给其他用户
            for (Map.Entry<String, UserSession> entry : userSessions.entrySet()) {
                String userId = entry.getKey();
                UserSession session = entry.getValue();

                if (!userId.equals(excludeUserId)) {
                    Channel channel = session.getChannel();
                    if (channel != null && channel.isActive()) {
                        // 使用 retainedDuplicate()：每个 Channel 拿到一个独立的副本
                        channel.writeAndFlush(frame.retainedDuplicate());
                    }
                }
            }
        } finally {
            // 确保原始帧被释放
            frame.release();
        }
    }

    //全局发送
    public void broadcastToAll(String docId, Object message) {
        // 参数检查
        if (docId == null || message == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }

        //根据 message 类型，生成对应的 WebSocket 帧
        Object frameToSend = null;

        if (message instanceof String) {
            //如果是字符串，包装成 TextWebSocketFrame
            frameToSend = new TextWebSocketFrame((String) message);
        }
        else if (message instanceof NettyMessage) {
            //如果是 NettyMessage，转成 JSON 再包装
            String json = ((NettyMessage) message).toJson();
            frameToSend = new TextWebSocketFrame(json);
        }
        else {
            //不支持的类型
            return; // 或 throw new IllegalArgumentException
        }

        //广播给所有人
        for (UserSession session : userSessions.values()) {
            Channel channel = session.getChannel();
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(frameToSend);
            }
        }
    }

    public void broadcastToAll(String docId, MessageResponse messageResponse) {
        // 参数检查（使用正确的变量名）
        if (docId == null || messageResponse == null) {
            return;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }

        //直接序列化 MessageResponse 为 JSON
        String json;
        try {
            json = messageResponse.toJson(); // 使用你定义的 toJson()
        } catch (Exception e) {
            e.printStackTrace(); // 建议使用 log.error("序列化失败", e);
            return;
        }

        // 不要共享同一个 Frame，每次 writeAndFlush 都要 new 新的
        for (UserSession session : userSessions.values()) {
            Channel channel = session.getChannel();
            if (channel != null && channel.isActive()) {
                // 每次发送都创建新的 TextWebSocketFrame
                channel.writeAndFlush(new TextWebSocketFrame(json));
            }
        }
    }

    //获取所有用户session
    public Collection<UserSession> getAllUserSessions(String docId) {
        Map<String, UserSession> userSessions = sessions.get(docId);
        return userSessions == null ? Collections.emptyList() : userSessions.values();
    }

    //获取channel
    public Channel getChannel(String docId, String userId) {
        UserSession session = findSessions(docId, userId);
        return session == null ? null : session.getChannel();
    }

    //查看用户是否在线
    public boolean isUserOnline(String docId, String userId) {
        if (docId == null || userId == null) {
            return false;
        }

        Map<String, UserSession> userSessions = sessions.get(docId);
        return userSessions != null && userSessions.containsKey(userId);
    }

    // 获取当前缓存的文档Id
    public List<String> getAllCacheDocIds() {
        return cacheDoc;
    }

    //添加缓存文档
    public void putCacheDocId(String docId){
        cacheDoc.add(docId);
    }

    //清空缓存数组
    public void clearCacheDocId(){
        cacheDoc.clear();
    }




}