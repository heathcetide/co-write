package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.ot.*;
import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import com.cowrite.project.netty.session.SessionManager;
import com.cowrite.project.netty.stream.NettyStreamProducer;
import com.cowrite.project.utils.RedisUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cowrite.project.netty.protocol.MessageType.USER_OFFLINE;
import static com.cowrite.project.netty.protocol.MessageType.USER_ONLINE;

@Component
public class ServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(ServerHandler.class);

    /**
     * Session Manager
     */
    private final SessionManager sessionManager;

    private final RedisUtils redisUtils;

    /**
     * Content Handler
     */
    private final ContentHandler contentHandler;

    /**
     * MessageHandler Factory
     * */
    private final MessageHandlerFactory handlerFactory;


    // 心跳超时时间（单位：秒）
    private static final int READ_IDLE_TIMEOUT = 300;

    public ServerHandler(SessionManager sessionManager, RedisUtils redisUtils,
                         ContentHandler contentHandler, MessageHandlerFactory handlerFactory) {
        this.sessionManager = sessionManager;
        this.redisUtils = redisUtils;
        this.contentHandler = contentHandler;
        this.handlerFactory = handlerFactory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) {
        NettyMessage message = NettyMessage.fromJson(msg.text());
        log.info("receive message {}", message);
        channelHandlerContext.channel().attr(SessionManager.USER_ID).set(message.getUserId());
        handleMessage(channelHandlerContext, message);
    }

    private void handleMessage(ChannelHandlerContext ctx, NettyMessage message) {
        String currentUserId = ctx.channel().attr(SessionManager.USER_ID).get();
        MessageType type = message.getOperationType();
        handlerFactory.getHandler(message.getOperationType()).handle(ctx, message);
    }
}