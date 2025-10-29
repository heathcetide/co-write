package com.cowrite.project.netty.ot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单向量时钟：记录各用户的版本号，支持自增、合并、因果 & 并发检测。
 */
public class VectorClock {
    private final Map<String, Long> time = new ConcurrentHashMap<>();
    public final String selfId;

    public VectorClock(String selfId) {
        this.selfId = selfId;
        time.put(selfId, 0L);
    }

    public void tick() {
        time.merge(selfId, 1L, Long::sum);
    }

    public void advance(VectorClock other) {
        other.time.forEach((k, v) -> time.merge(k, v, Math::max));
    }

    public boolean canAdvance(VectorClock other) {
        for (Map.Entry<String, Long> e : other.time.entrySet()) {
            if (e.getKey().equals(other.selfId)) continue;
            if (time.getOrDefault(e.getKey(), 0L) < e.getValue()) return false;
        }
        return true;
    }

    public boolean happenedBefore(VectorClock other) {
        boolean less = false;
        Set<String> keys = new HashSet<>(time.keySet());
        keys.addAll(other.time.keySet());
        for (String u : keys) {
            long a = time.getOrDefault(u, 0L);
            long b = other.time.getOrDefault(u, 0L);
            if (a > b) return false;
            if (a < b) less = true;
        }
        return less;
    }

    public boolean isConcurrent(VectorClock other) {
        return !happenedBefore(other) && !other.happenedBefore(this);
    }
}
