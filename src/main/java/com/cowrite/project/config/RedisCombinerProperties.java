package com.cowrite.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisCombinerProperties {

    @Value("${cowrite.redis.combiner.max-batch:50}")
    private int maxBatch;

    @Value("${cowrite.redis.combiner.max-flush-millis:50}")
    private long maxFlushMillis;

    @Value("${cowrite.redis.combiner.scan-pattern:doc:*:buf}")
    private String scanPattern;

    @Value("${cowrite.redis.combiner.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Value("${cowrite.redis.combiner.scheduler.fixed-delay-ms:1000}")
    private long schedulerFixedDelayMs;

    public int getMaxBatch() { return maxBatch; }
    public long getMaxFlushMillis() { return maxFlushMillis; }
    public String getScanPattern() { return scanPattern; }
    public boolean isSchedulerEnabled() { return schedulerEnabled; }
    public long getSchedulerFixedDelayMs() { return schedulerFixedDelayMs; }
}


