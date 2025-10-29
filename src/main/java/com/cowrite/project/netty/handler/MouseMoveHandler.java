package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.ot.OTEngine;
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
public class MouseMoveHandler implements MessageHandler{

    private final SessionManager sessionManager;
    private final ContentHandler contentHandler;
    private final Map<String, OTEngine> engines = new ConcurrentHashMap<>();
    private final Map<String, VectorClock> clientClocks= new ConcurrentHashMap<>();

    // 构造函数注入依赖
    public MouseMoveHandler(SessionManager sessionManager,
                             ContentHandler contentHandler) {
        this.sessionManager = sessionManager;
        this.contentHandler = contentHandler;
    }
    @Override
    public MessageType getType() {
        return MessageType.MOUSE_MOVE;
    }


    @Override
    public void handle(ChannelHandlerContext ctx, NettyMessage message) {
        // 原handleContentChange中的业务逻辑
        //检查用户是否在线
        if (!sessionManager.isUserOnline(message.getDocId(), message.getUserId())) {
            return;
        }
        HashMap<String, Object> userPayload = new HashMap<>();
        userPayload.put("x", message.getX());
        userPayload.put("y", message.getY());

        MessageResponse userMessageResponse = new MessageResponse.Builder()
                .docId(message.getDocId())
                .userId(message.getUserId())
                .type(message.getOperationType())
                .timestamp(message.getTimestamp())
                .payload(userPayload)
                .build();

        sessionManager.sendMessageToOthers(message.getDocId(), message.getUserId(), userMessageResponse);
    }

}
