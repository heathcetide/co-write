package com.cowrite.project.netty.stream;

import com.cowrite.project.netty.handler.MessageHandler;
import com.cowrite.project.netty.handler.MessageHandlerFactory;
import com.cowrite.project.netty.protocol.MessageType;
import com.cowrite.project.netty.protocol.NettyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class RedisStreamConsumerManager {

    private static final Logger log = LoggerFactory.getLogger(RedisStreamConsumerManager.class);

    private static final String GROUP_NAME = "doc-group";

    private static final int THREAD_POOL_SIZE = 8;
    private static final int POLL_COUNT = 100;
    private static final Duration BLOCK_TIMEOUT = Duration.ofMillis(300);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private DocumentExecutorManager documentExecutorManager;

    private final MessageHandlerFactory handlerFactory;

    private final ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public RedisStreamConsumerManager(MessageHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @PostConstruct
    public void init() {
        List<String> docIds = getAllActiveDocIds();

        for (String docId : docIds) {
            String streamKey = "doc:" + docId + ":stream";

            // 保证消费组存在
            try {
                redisTemplate.opsForStream().createGroup(streamKey, GROUP_NAME);
            } catch (Exception e) {
                log.debug("消费组已存在: {}", streamKey);
            }

            String consumerName = "consumer-" + docId;

            // 每个文档一个异步消费线程，使用线程池管理
            consumerPool.submit(() -> consumeLoop(streamKey, docId, consumerName));
        }
    }

    private List<String> getAllActiveDocIds() {
        // TODO: 可从数据库或Redis中动态获取活跃文档列表
        return List.of("1", "2", "3");
    }

    private void consumeLoop(String streamKey, String docId, String consumerName) {
        while (true) {
            try {
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                        Consumer.from(GROUP_NAME, consumerName),
                        StreamReadOptions.empty().count(POLL_COUNT).block(BLOCK_TIMEOUT),
                        StreamOffset.create(streamKey, ReadOffset.lastConsumed())
                );

                if (records != null && !records.isEmpty()) {
                    for (MapRecord<String, Object, Object> record : records) {
                        try {
                            NettyMessage message;
                            if (record.getValue() != null && record.getValue().containsKey("payload")) {
                                Object payload = record.getValue().get("payload");
                                message = NettyMessage.fromJson(String.valueOf(payload));
                            } else {
                                message = NettyMessage.fromMap(record.getValue());
                            }
                            documentExecutorManager.execute(docId, () -> {
                                MessageHandler handler = handlerFactory.getHandler(message.getOperationType());
                                if (handler != null) {
                                    handler.handle(null, message); // ctx 为空，可用 DummyCtx 替代
                                } else {
                                    log.warn("未找到处理器: {}", message.getOperationType());
                                }
                            });

                            // ACK确认
                            redisTemplate.opsForStream().acknowledge(streamKey, GROUP_NAME, record.getId());
                        } catch (Exception e) {
                            log.error("处理消息失败: {}", record, e);
                        }
                    }
                }

            } catch (Exception e) {
                log.error("拉取Redis Stream异常: {}", streamKey, e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
