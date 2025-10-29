package com.cowrite.project.netty.protocol;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.cowrite.project.utils.JsonUtils.fromJson;
import static com.cowrite.project.utils.JsonUtils.toJson;


/**
 * 消息基类 用于在Netty网络通信中传输的文档操作消息
 *
 * @author heathcetide
 */
public class NettyMessage {

    /**
     * 操作类型
     */
    private MessageType operationType;

    /**
     * 文档ID
     */
    private String docId;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 修改内容
     */
    private String content;

    /**
     * 删除长度
     */
    private Integer length;

    /**
     * 操作位置
     */
    private Integer pos;

    /**
     * 光标位置
     */
    private Integer x;
    private Integer y;

    /**
     * 修改时间
     */
    private Long timestamp;

    /**
     * 输入状态
     */
    private boolean isTyping;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MessageType getOperationType() {
        return operationType;
    }

    public void setOperationType(MessageType operationType) {
        this.operationType = operationType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }


    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setIsTyping(boolean typing) {
        isTyping = typing;
    }

    public int getInsertLength() {
        return content == null ? 0 : content.length();
    }

    public NettyMessage() {
    }

    public NettyMessage(MessageType operationType, String docId, String userId, String content,Integer length, Integer pos, Integer x, Integer y, Long timestamp, boolean isTyping) {
        this.operationType = operationType;
        this.docId = docId;
        this.userId = userId;
        this.content = content;
        this.length = length;
        this.pos = pos;
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
        this.isTyping = isTyping;
    }

    public NettyMessage(NettyMessage op) {
        this.operationType = op.getOperationType();
        this.docId = op.getDocId();
        this.userId = op.getUserId();
        this.content = op.getContent();
        this.length = op.getLength();
        this.pos = op.getPos();
        this.x = op.getX();
        this.y = op.getY();
        this.timestamp = op.getTimestamp();
        this.isTyping = op.isTyping();
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static NettyMessage fromJson(String json) {
        try {
            return mapper.readValue(json, NettyMessage.class);
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

    /**
     * 获取建造者实例
     * @return Builder对象
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * NettyMessage的建造者类
     */
    public static class Builder {
        private MessageType operationType;
        private String docId;
        private String userId;
        private String content;
        private Integer length;
        private Integer pos;
        private Integer x;
        private Integer y;
        private Long timestamp;
        private boolean isTyping;

        public Builder() {
        }

        /**
         * 设置操作类型
         * @param operationType 操作类型
         * @return Builder实例
         */
        public Builder operationType(MessageType operationType) {
            this.operationType = operationType;
            return this;
        }

        /**
         * 设置文档ID
         * @param docId 文档ID
         * @return Builder实例
         */
        public Builder docId(String docId) {
            this.docId = docId;
            return this;
        }

        /**
         * 设置用户ID
         * @param userId 用户ID
         * @return Builder实例
         */
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        /**
         * 设置操作内容
         * @param content 操作内容
         * @return Builder实例
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * 设置删除长度
         * @param length 删除长度
         * @return Builder实例
         */
        public Builder length(Integer length) {
            this.length = length;
            return this;
        }

        /**
         * 设置操作位置
         * @param pos 操作位置
         * @return Builder实例
         */
        public Builder pos(Integer pos) {
            this.pos = pos;
            return this;
        }

        /**
         * 设置光标x坐标
         * @param x 光标x坐标
         * @return Builder实例
         */
        public Builder x(Integer x) {
            this.x = x;
            return this;
        }

        /**
         * 设置光标y坐标
         * @param y 光标y坐标
         * @return Builder实例
         */
        public Builder y(Integer y) {
            this.y = y;
            return this;
        }

        /**
         * 设置时间戳
         * @param timestamp 时间戳
         * @return Builder实例
         */
        public Builder timestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * 设置输入状态
         * @param isTyping 输入状态
         * @return Builder实例
         */
        public Builder isTyping(boolean isTyping) {
            this.isTyping = isTyping;
            return this;
        }

        /**
         * 构建NettyMessage对象
         * @return 构建完成的NettyMessage实例
         */
        public NettyMessage build() {
            return new NettyMessage(operationType, docId, userId, content, length, pos, x, y, timestamp, isTyping);
        }
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "operationType=" + operationType +
                ", docId='" + docId + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", length=" + length +
                ", pos=" + pos +
                ", x=" + x +
                ", y=" + y +
                ", timestamp=" + timestamp +
                ", isTyping=" + isTyping +
                '}';
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("operationType", operationType != null ? operationType : null);
        map.put("docId", docId);
        map.put("userId", userId);
        map.put("content", content);
        map.put("length", length);
        map.put("pos", pos);
        map.put("x", x);
        map.put("y", y);
        map.put("timestamp", timestamp);
        map.put("isTyping", isTyping);
        return map;
    }

    public static NettyMessage fromMap(Map<Object, Object> map) {
        if (map == null) return null;

        NettyMessage message = new NettyMessage();

        Object opType = map.get("operationType");
        if (opType instanceof String) {
            message.setOperationType(MessageType.valueOf((String) opType));
        } else if (opType instanceof MessageType) {
            message.setOperationType((MessageType) opType);
        }

        message.setDocId((String) map.get("docId"));
        message.setUserId((String) map.get("userId"));
        message.setContent((String) map.get("content"));

        message.setLength(castToInteger(map.get("length")));
        message.setPos(castToInteger(map.get("pos")));
        message.setX(castToInteger(map.get("x")));
        message.setY(castToInteger(map.get("y")));
        message.setTimestamp(castToLong(map.get("timestamp")));

        Object typing = map.get("isTyping");
        if (typing instanceof Boolean) {
            message.setIsTyping((Boolean) typing);
        } else if (typing instanceof String) {
            message.setIsTyping(Boolean.parseBoolean((String) typing));
        }

        return message;
    }

    private static Integer castToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long castToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }



}