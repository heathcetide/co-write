package com.cowrite.project.netty.ot;

import com.cowrite.project.netty.protocol.MessageType;

/**
 * 最基础的 OT 变换：只在并发时调整位置，不改内容长度。
 */
public class Transformer {
    /**
     * 只在并发时移动 pos，不改内容
     */
    public static void transform(Operation op, Operation prev) {
        if (prev.type == MessageType.CONTENT_INSERT) {
            if (prev.pos <= op.pos) {
                op.pos += prev.content.length();
            }
        } else if (prev.type == MessageType.CONTENT_DELETE) {
            if (prev.pos < op.pos) {
                op.pos -= Math.min(prev.length, op.pos - prev.pos);
            }
        }
    }
}
