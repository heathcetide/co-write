package com.cowrite.project.netty.stream;

import com.cowrite.project.netty.protocol.NettyMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import com.cowrite.project.config.RedisCombinerProperties;
import java.util.HashMap;
import java.util.Map;

@Component
public class NettyStreamProducer {

    @Resource
    private RedisWriteCombiner writeCombiner;

    @Resource
    private RedisCombinerProperties combinerProperties;

    public void publishToStream(NettyMessage message) {
        // Serialize as JSON string payload for script
        String payload = message.toJson();
        writeCombiner.execute(String.valueOf(message.getDocId()), payload,
                combinerProperties.getMaxBatch(), combinerProperties.getMaxFlushMillis());
    }
}
