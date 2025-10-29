package com.cowrite.project.interceptor;

import com.cowrite.project.common.anno.Idempotent;
import com.cowrite.project.exception.UnauthorizedException;
import com.cowrite.project.utils.RedisUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class RequestParamIdempotentStrategy extends AbstractIdempotentStrategy {

    @Resource
    private  RedisUtils redisUtils;

    @Override
    protected void validateRequest(HttpServletRequest request, String token, Idempotent methodAnnotation, int timeout, int expire) throws UnauthorizedException {
        String identifier = methodAnnotation.identifier();
        String requestParam = request.getParameter(identifier.isEmpty() ? "id" : identifier); // 使用指定的标识
        String redisKey = methodAnnotation.prefix() + DigestUtils.md5Hex(token + requestParam);
        long currentTime = System.currentTimeMillis();
        long lastRequestTime = (Long) redisUtils.get(redisKey + ":lastRequestTime");

        // 防抖和节流逻辑
        if (currentTime - lastRequestTime < timeout * 1000) {
            throw new UnauthorizedException(methodAnnotation.message());
        }

        // 更新请求时间
        redisUtils.set(redisKey + ":lastRequestTime", currentTime, timeout);

        if (redisUtils.exists(redisKey)) {
            throw new UnauthorizedException("请勿重复提交");
        } else {
            redisUtils.set(redisKey, request.getRequestURI(), expire);
        }
    }
} 