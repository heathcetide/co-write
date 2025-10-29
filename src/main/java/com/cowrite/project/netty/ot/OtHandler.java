package com.cowrite.project.netty.ot;

import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class OtHandler {

    public NettyMessage[] transform(NettyMessage newOp, NettyMessage oldOp) {
        if (newOp == null || oldOp == null) {
            throw new IllegalArgumentException("Operations cannot be null.");
        }

        MessageType newType = newOp.getOperationType();
        MessageType oldType = oldOp.getOperationType();

        if (newType == MessageType.CONTENT_INSERT) {
            if (oldType == MessageType.CONTENT_INSERT) {
                return transformNewInsert_OldInsert(newOp, oldOp);
            } else if (oldType == MessageType.CONTENT_DELETE) {
                return transformNewInsert_OldDelete(newOp, oldOp);
            }
        } else if (newType == MessageType.CONTENT_DELETE) {
            if (oldType == MessageType.CONTENT_INSERT) {
                return transformNewDelete_OldInsert(newOp, oldOp);
            } else if (oldType == MessageType.CONTENT_DELETE) {
                return transformNewDelete_OldDelete(newOp, oldOp);
            }
        }

        throw new IllegalArgumentException("Unsupported operation types: " + newType + " vs " + oldType);
    }

    // ========================================================================
    // Case 1: 新操作是 INSERT, 旧操作是 INSERT
    // 旧插入发生在位置 P，长度 L。
    // 如果新插入位置 >= P，则新位置 += L。
    // ========================================================================
    private NettyMessage[] transformNewInsert_OldInsert(NettyMessage newInsert, NettyMessage oldInsert) {
        NettyMessage adjustedNew = copy(newInsert);
        NettyMessage old = copy(oldInsert); // 旧操作保持不变

        int newPos = newInsert.getPos();
        int oldPos = oldInsert.getPos();
        int oldLen = oldInsert.getInsertLength();

        if (newPos >= oldPos) {
            // 新插入点在旧插入点之后或相同 → 需要后移
            adjustedNew.setPos(newPos + oldLen);
        }
        // 如果 newPos < oldPos，不需要调整

        return new NettyMessage[]{adjustedNew, old};
    }

    // ========================================================================
    // Case 2: 新操作是 INSERT, 旧操作是 DELETE
    // 旧删除从 P 开始，长度 L。
    // 如果新插入位置 > P，则新位置 -= L (因为前面删了L个字符)。
    // 如果新插入位置 <= P，则不需要调整（插入在删除前或起点）。
    // ========================================================================
    private NettyMessage[] transformNewInsert_OldDelete(NettyMessage newInsert, NettyMessage oldDelete) {
        NettyMessage adjustedNew = copy(newInsert);
        NettyMessage old = copy(oldDelete); // 旧操作保持不变

        int newPos = newInsert.getPos();
        int oldPos = oldDelete.getPos();
        int oldLen = getDeleteLength(oldDelete);

        if (newPos > oldPos) {
            // 新插入点在旧删除起点之后 → 需要前移
            adjustedNew.setPos(newPos - oldLen);
        }
        // 如果 newPos <= oldPos，不需要调整

        return new NettyMessage[]{adjustedNew, old};
    }

    // ========================================================================
    // Case 3: 新操作是 DELETE, 旧操作是 INSERT
    // 旧插入发生在位置 P，长度 L。
    // 如果新删除起点 >= P，则新起点 += L。
    // 注意：新删除的范围可能覆盖旧插入的内容，但起点位置需要调整。
    // ========================================================================
    private NettyMessage[] transformNewDelete_OldInsert(NettyMessage newDelete, NettyMessage oldInsert) {
        NettyMessage adjustedNew = copy(newDelete);
        NettyMessage old = copy(oldInsert); // 旧操作保持不变

        int newDelPos = newDelete.getPos();
        int oldInsPos = oldInsert.getPos();
        int oldInsLen = oldInsert.getInsertLength();

        if (newDelPos >= oldInsPos) {
            // 新删除起点在旧插入点之后或相同 → 需要后移
            adjustedNew.setPos(newDelPos + oldInsLen);
        }
        // 如果 newDelPos < oldInsPos，不需要调整

        return new NettyMessage[]{adjustedNew, old};
    }

    // ========================================================================
    // Case 4: 新操作是 DELETE, 旧操作是 DELETE
    // 旧删除 [P_old, P_old + L_old)
    // 新删除 [P_new, P_new + L_new)
    // 情况复杂，需要判断重叠。
    // 核心：调整新删除的起点和长度，使其在旧删除后执行。
    // ========================================================================
    private NettyMessage[] transformNewDelete_OldDelete(NettyMessage newDelete, NettyMessage oldDelete) {
        NettyMessage adjustedNew = copy(newDelete);
        NettyMessage old = copy(oldDelete); // 旧操作保持不变

        int newDelPos = newDelete.getPos();
        int newDelLen = getDeleteLength(newDelete);
        int oldDelPos = oldDelete.getPos();
        int oldDelLen = getDeleteLength(oldDelete);
        int oldDelEnd = oldDelPos + oldDelLen;

        if (newDelPos >= oldDelEnd) {
            // 新删除完全在旧删除之后 → 新起点前移
            adjustedNew.setPos(newDelPos - oldDelLen);
        } else if (newDelPos < oldDelPos) {
            // 新删除起点在旧删除之前
            if (newDelPos + newDelLen <= oldDelPos) {
                // 新删除完全在旧删除之前 → 不需要调整
            } else {
                // 新删除与旧删除重叠（新删除起点在旧前，终点在旧内或后）
                // 调整新删除长度：减去与旧删除重叠的部分
                int overlapStart = oldDelPos;
                int overlapEnd = Math.min(newDelPos + newDelLen, oldDelEnd);
                int overlapLen = overlapEnd - overlapStart;
                adjustedNew.setLength(newDelLen - overlapLen);
                // 起点不变
            }
        } else {
            // newDelPos >= oldDelPos && newDelPos < oldDelEnd
            // 新删除起点在旧删除范围内
            // 此时，旧删除已经移除了这部分文本，新删除应被取消或调整
            // 通常做法：取消新删除（因为目标文本已不存在）
            adjustedNew = null;
            // 或者：将新删除起点调整到旧删除末尾？但语义不清晰，通常取消
        }

        return filterNull(adjustedNew, old);
    }

    // ========================================================================
    // 工具方法
    // ========================================================================

    private int getDeleteLength(NettyMessage op) {
        if (op.getOperationType() != MessageType.CONTENT_DELETE) return 0;
        return op.getLength() != null ? op.getLength() : 0;
    }

    private NettyMessage copy(NettyMessage src) {
        return src == null ? null : new NettyMessage(src);
    }

    private NettyMessage[] filterNull(NettyMessage a, NettyMessage b) {
        if (a == null && b == null) return new NettyMessage[0];
        if (a == null) return new NettyMessage[]{null, b}; // 注意：这里返回 [null, b]，调用方需处理 a 为 null
        if (b == null) return new NettyMessage[]{a, null};
        return new NettyMessage[]{a, b};
    }

    // --- 可选：用于调试 ---
    public boolean isIndependent(NettyMessage op1, NettyMessage op2) {
        // 独立性判断逻辑可以保留，但注意调用顺序可能影响结果
        throw new UnsupportedOperationException("isIndependent not implemented for this transform order.");
    }
}