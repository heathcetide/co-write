package com.cowrite.project.netty.ot;/*
 * Lightweight OT + Vector Clock implementation for lock-free conflict merging
 * - Operation segmentation
 * - Vector clock for causality & concurrency detection
 * - Local buffer for out-of-order ops
 * - Conflict resolution under 30ms
 */

import com.cowrite.project.netty.protocol.MessageType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * 一个集中式的 OT 引擎，用单一的 serverClock 来管理因果，
 * 并对并发操作做位置变换。
 */
public class OTEngine {
    private final VectorClock serverClock = new VectorClock("server");
    private final List<Operation> history = Collections.synchronizedList(new ArrayList<>());
    private final Queue<Operation> incoming = new ConcurrentLinkedQueue<>();
    private final StringBuilder doc = new StringBuilder();

    public synchronized void receive(Operation op) {
        incoming.add(op);
        process();
    }

    private void process() {
        Operation op;
        while ((op = incoming.poll()) != null) {
            if (!serverClock.canAdvance(op.clock)) {
                incoming.add(op);
                break;
            }
            long start = System.nanoTime();

            for (Operation prev : history) {
                // **只对其他用户** 的并发操作做 transform，本用户自己的操作跳过**
                if (!prev.userId.equals(op.userId)
                        && op.clock.isConcurrent(prev.clock)) {
                    Transformer.transform(op, prev);
                }
            }

            applyToDoc(op);
            history.add(op);
            serverClock.advance(op.clock);

            long dur = (System.nanoTime() - start) / 1_000_000;
            if (dur > 30) {
                System.err.println("Warning: OT took " + dur + "ms");
            }
        }
    }

    private void applyToDoc(Operation op) {
        switch (op.type) {
            case CONTENT_INSERT:
                // clamp pos 到 [0, doc.length()]
                int ipos = Math.max(0, Math.min(op.pos, doc.length()));
                doc.insert(ipos, op.content);
                break;
            case CONTENT_DELETE:
                int start = Math.max(0, op.pos);
                int end = Math.min(op.pos + op.length, doc.length());
                if (start < end) {
                    doc.delete(start, end);
                }
                break;
            case CURSOR_MOVE:
                break;
        }
    }

    public synchronized String getDocument() {
        return doc.toString();
    }
}


