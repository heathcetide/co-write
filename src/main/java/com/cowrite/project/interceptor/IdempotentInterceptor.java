package com.cowrite.project.interceptor;

import com.cowrite.project.common.anno.Idempotent;
import com.cowrite.project.config.IdempotentConfig;
import com.cowrite.project.exception.UnauthorizedException;
import com.cowrite.project.utils.JwtUtils;
import com.cowrite.project.utils.RedisUtils;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;

@Component
public class IdempotentInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class);

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private IdempotentConfig idempotentConfig;

    @Resource
    private RateLimiter rateLimiter;

    private final IdempotentStrategy idempotentStrategy;

    public IdempotentInterceptor() {
        this.idempotentStrategy = IdempotentStrategyFactory.createStrategy("default");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!rateLimiter.tryAcquire()) {
            throw new UnauthorizedException("请求过于频繁，请稍后再试");
        }
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Idempotent methodAnnotation = method.getAnnotation(Idempotent.class);
        //方法上面没有添加这个注解，则不拦截
        if (Objects.isNull(methodAnnotation)){
            return true;
        }
        
        // 使用配置中的超时时间和过期时间
        int timeout = methodAnnotation.timeout() > 0 ? methodAnnotation.timeout() : idempotentConfig.getTimeout();
        int expire = methodAnnotation.expire() > 0 ? methodAnnotation.expire() : idempotentConfig.getExpire();

        String token = request.getHeader("Authorization");
        if (token == null ) {
            throw new UnauthorizedException("未登录或token已过期");
        }
        // 验证token
        if (token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        if (!jwtUtils.validateToken(token)) {
            throw new UnauthorizedException("token无效或已过期");
        }
        try {
            // 将超时时间和过期时间传递给策略
            idempotentStrategy.handle(request, token, methodAnnotation, timeout, expire);
        } catch (UnauthorizedException e) {
            logger.error("请求处理失败: {}", e.getMessage());
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 可以在这里记录日志
        logger.info("请求完成: {} {}", request.getMethod(), request.getRequestURI());
    }
}
























