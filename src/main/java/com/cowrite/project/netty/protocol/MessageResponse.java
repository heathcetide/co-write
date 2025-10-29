package com.cowrite.project.netty.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息基类
 */
public class MessageResponse {

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 文档ID
     */
    private String docId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 消息时间戳
     */
    private long timestamp;

    /**
     * 消息负载
     */
    private Map<String, Object> payload = new HashMap<>();

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public static class Builder {
        private final MessageResponse instance;

        public Builder() {
            instance = new MessageResponse();
        }

        public Builder type(MessageType type) {
            instance.type = type;
            return this;
        }

        public Builder docId(String docId) {
            instance.docId = docId;
            return this;
        }

        public Builder userId(String userId) {
            instance.userId = userId;
            return this;
        }

        public Builder timestamp(long timestamp) {
            instance.timestamp = timestamp;
            return this;
        }

        public Builder payload(Map<String, Object> payload) {
            instance.payload = payload;
            return this;
        }

        // 方便单条添加
        public Builder putPayload(String key, Object value) {
            instance.payload.put(key, value);
            return this;
        }

        public MessageResponse build() {
            // 如果timestamp没设，默认设置当前时间
            if (instance.timestamp == 0) {
                instance.timestamp = System.currentTimeMillis();
            }
            return instance;
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static MessageResponse fromJson(String json) {
        try {
            return mapper.readValue(json, MessageResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 解析失败: " + json, e);
        }
    }

    public String toJson() {
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "type=" + type +
                ", docId='" + docId + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}