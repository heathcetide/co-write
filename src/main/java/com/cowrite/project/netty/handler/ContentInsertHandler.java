package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.ot.OTEngine;
import com.cowrite.project.netty.ot.Operation;
import com.cowrite.project.netty.ot.VectorClock;
import com.cowrite.project.netty.protocol.MessageResponse;
import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import com.cowrite.project.netty.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContentInsertHandler implements MessageHandler {

    private final SessionManager sessionManager;
    private final ContentHandler contentHandler;
    private final Map<String, OTEngine> engines = new ConcurrentHashMap<>();
    private final Map<String, VectorClock> clientClocks= new ConcurrentHashMap<>();

    // 构造函数注入依赖
    public ContentInsertHandler(SessionManager sessionManager,
                            ContentHandler contentHandler) {
        this.sessionManager = sessionManager;
        this.contentHandler = contentHandler;
    }
    @Override
    public MessageType getType() {
        return MessageType.CONTENT_INSERT;
    }


    @Override
    public void handle(ChannelHandlerContext ctx, NettyMessage message) {
        // 原handleContentChange中的业务逻辑
        // nettyStreamProducer.publishToStream(message);
        String docId  = message.getDocId();
        String userId = message.getUserId();

        // 1. 拿或建引擎
        OTEngine engine = engines.computeIfAbsent(docId, id -> new OTEngine());

        // 2. 拿或建时钟
        VectorClock vc = clientClocks.computeIfAbsent(userId, VectorClock::new);

        // 3. 本地打点
        vc.tick();

        // 4. 构造 OT Operation
        MessageType type = MessageType.valueOf(message.getOperationType().name());
        Operation op = new Operation(
                docId,
                userId,
                type,
                message.getContent(),
                message.getLength() != null ? message.getLength() : 0,
                message.getPos()    != null ? message.getPos()    : 0,
                message.getTimestamp() != null ? message.getTimestamp() : System.currentTimeMillis(),
                vc
        );

        // 5. 送入 OT
        engine.receive(op);

        HashMap<String, Object> userPayload = new HashMap<>();
        userPayload.put("content", op.content);
        userPayload.put("pos", op.pos);
        userPayload.put("length", op.length);
        sessionManager.sendMessageToOthers(docId, userId, new MessageResponse.Builder()
                .docId(message.getDocId())
                .userId(message.getUserId())
                .type(message.getOperationType())
                .timestamp(op.timestamp)
                .payload(userPayload)
                .build());
    }
}
