package com.cowrite.project.netty.ot;

import com.cowrite.project.netty.protocol.MessageType;

/**
 * 一次客户端提交的操作 —— 必须外部传入它对应的 VectorClock。
 */
public class Operation {
    public final String docId;
    public final String userId;
    public final MessageType type;
    public final String content;
    public int length;  // 对 delete 有效
    public int pos;
    public final long timestamp;
    public final VectorClock clock;

    public Operation(String docId,
                     String userId,
                     MessageType type,
                     String content,
                     int length,
                     int pos,
                     long timestamp,
                     VectorClock clock) {
        this.docId = docId;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.length = length;
        this.pos = pos;
        this.timestamp = timestamp;
        this.clock = clock;
    }
}
