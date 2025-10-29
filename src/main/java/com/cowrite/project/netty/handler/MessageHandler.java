package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler {
    MessageType getType();

    void handle(ChannelHandlerContext ctx, NettyMessage message);
}
