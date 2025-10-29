package com.cowrite.project.netty.handler;

import com.cowrite.project.netty.protocol.MessageType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageHandlerFactory {
    private final Map<MessageType, MessageHandler> handlerMap;

    private final ContentDeleteHandler deleteHandler;

    private final MouseMoveHandler mouseMoveHandler;

    private final UserOfflineHandler userOfflineHandler;

    private final UserOnlineHandler userOnlineHandler;

    @PostConstruct
    public void initHandlerMap(){
        handlerMap.put(MessageType.USER_ONLINE, userOnlineHandler);
        handlerMap.put(MessageType.USER_OFFLINE, userOfflineHandler);
        handlerMap.put(MessageType.CONTENT_DELETE, deleteHandler);
        handlerMap.put(MessageType.CONTENT_INSERT, deleteHandler);
        handlerMap.put(MessageType.MOUSE_MOVE, mouseMoveHandler);
    }


    // 自动注入所有MessageHandler实现类
    public MessageHandlerFactory (List<MessageHandler> handlers, ContentDeleteHandler deleteHandler, MouseMoveHandler mouseMoveHandler, UserOfflineHandler userOfflineHandler, UserOnlineHandler userOnlineHandler) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(
                        MessageHandler::getType,
                        Function.identity()
                ));
        this.deleteHandler = deleteHandler;
        this.mouseMoveHandler = mouseMoveHandler;
        this.userOfflineHandler = userOfflineHandler;
        this.userOnlineHandler = userOnlineHandler;
    }

    // 获取对应处理器
    public MessageHandler getHandler(MessageType type) {
        return handlerMap.get(type);
    }
}
