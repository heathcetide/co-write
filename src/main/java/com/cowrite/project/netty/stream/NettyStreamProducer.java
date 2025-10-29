package com.cowrite.project.netty.stream;

import com.cowrite.project.netty.protocol.NettyMessage;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class NettyStreamProducer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void publishToStream(NettyMessage message) {
        redisTemplate.opsForStream().add(StreamRecords.newRecord()
                .in("doc:" + message.getDocId() + ":stream")
                .ofMap(message.toMap()));
    }
}
