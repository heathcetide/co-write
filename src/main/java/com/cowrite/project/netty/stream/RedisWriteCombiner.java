package com.cowrite.project.netty.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Per-document write combiner using Redis Lua for atomic enqueue + conditional flush to Stream.
 * Combines up to maxBatch operations or flushes after maxFlushMillis.
 */
@Component
public class RedisWriteCombiner {

    private static final Logger log = LoggerFactory.getLogger(RedisWriteCombiner.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<List> script;

    @PostConstruct
    public void init() {
        // KEYS[1]=buffer list key, KEYS[2]=lastFlushTs key, KEYS[3]=stream key
        // ARGV[1]=nowMillis, ARGV[2]=maxBatch, ARGV[3]=maxFlushMillis, ARGV[4]=payload
        final String lua =
            "local buf=KEYS[1]; local last=KEYS[2]; local stream=KEYS[3];" +
            "local now=tonumber(ARGV[1]); local maxBatch=tonumber(ARGV[2]); local maxMs=tonumber(ARGV[3]); local payload=ARGV[4];" +
            "if payload and string.len(payload) > 0 then redis.call('LPUSH', buf, payload); end " +
            "local len=tonumber(redis.call('LLEN', buf));" +
            "local lastTs=tonumber(redis.call('GET', last) or '0');" +
            "local shouldFlush = (len >= maxBatch) or (now - lastTs >= maxMs);" +
            "local flushed = 0;" +
            "if shouldFlush then " +
            "  local n = math.min(len, maxBatch);" +
            "  for i=1,n do " +
            "    local item = redis.call('RPOP', buf);" +
            "    if item then redis.call('XADD', stream, '*', 'payload', item); flushed = flushed + 1; end " +
            "  end " +
            "  redis.call('SET', last, tostring(now));" +
            "end " +
            "return {flushed, len - flushed}";

        script = new DefaultRedisScript<>();
        script.setScriptText(lua);
        script.setResultType(List.class);
    }

    public List execute(String docId, String payload, int maxBatch, long maxFlushMillis) {
        String bufKey = "doc:" + docId + ":buf";
        String lastKey = "doc:" + docId + ":lastflush";
        String streamKey = "doc:" + docId + ":stream";

        long now = System.currentTimeMillis();
        List<String> keys = Arrays.asList(bufKey, lastKey, streamKey);
        List<Object> args = Arrays.asList(String.valueOf(now), String.valueOf(maxBatch), String.valueOf(maxFlushMillis), payload);

        Object result = redisTemplate.execute(script, keys, args.toArray());
        if (result instanceof List) {
            return (List) result;
        }
        return Collections.emptyList();
    }
}


