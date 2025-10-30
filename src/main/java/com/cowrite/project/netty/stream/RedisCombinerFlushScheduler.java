package com.cowrite.project.netty.stream;

import com.cowrite.project.config.RedisCombinerProperties;
import com.cowrite.project.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class RedisCombinerFlushScheduler {

    private static final Logger log = LoggerFactory.getLogger(RedisCombinerFlushScheduler.class);

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedisWriteCombiner writeCombiner;

    @Resource
    private RedisCombinerProperties props;

    @Scheduled(fixedDelayString = "${cowrite.redis.combiner.scheduler.fixed-delay-ms:1000}")
    public void flushStaleBuffers() {
        if (!props.isSchedulerEnabled()) return;
        try {
            Set<String> keys = redisUtils.scanKeys(props.getScanPattern());
            for (String key : keys) {
                // key format: doc:{docId}:buf
                int start = key.indexOf(":");
                int end = key.lastIndexOf(":");
                if (start > -1 && end > start) {
                    String docId = key.substring(start + 1, end);
                    writeCombiner.execute(docId, "", props.getMaxBatch(), props.getMaxFlushMillis());
                }
            }
        } catch (Exception e) {
            log.warn("flushStaleBuffers error", e);
        }
    }
}


