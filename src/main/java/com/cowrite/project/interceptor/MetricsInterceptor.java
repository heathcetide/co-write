package com.cowrite.project.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MetricsInterceptor.class);

    private final StringRedisTemplate redisTemplate;

    public MetricsInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String date = LocalDate.now().toString();
        int hour = LocalTime.now().getHour();
        int second = LocalTime.now().getSecond();

        String ip = request.getRemoteAddr(); // 用 IP 或者 User ID 代替
        // 记录 PV
        redisTemplate.opsForValue().increment("metrics:pv:" + date);

        // 记录 UV
        String uvId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("uv_id".equals(cookie.getName())) {
                    uvId = cookie.getValue();
                    break;
                }
            }
        }
        if (uvId == null) {
            uvId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("uv_id", uvId);
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 天有效
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        redisTemplate.opsForHyperLogLog().add("metrics:uv:" + date, uvId);
        // QPS
        redisTemplate.opsForList().rightPush("metrics:qps:" + date + ":" + hour, String.valueOf(second));

        // Bounce Rate
        String userId = request.getSession().getId(); // 或者使用用户 ID
        redisTemplate.opsForSet().add("metrics:bounce:" + date, userId);
        redisTemplate.opsForValue().increment("metrics:total_visits:" + date);

        // Conversion Rate (用户完成购买、注册等操作时触发)
        redisTemplate.opsForValue().increment("metrics:conversions:" + date);
        redisTemplate.opsForValue().increment("metrics:total_visits:" + date);

        // Avg Response Time (记录每次请求的响应时间)
        long startTime = System.nanoTime();
        response.addHeader("X-Request-Start", String.valueOf(startTime)); // 可选：记录开始时间

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // 获取请求开始时间，检查 null 并设置默认值
        String startTimeStr = request.getHeader("X-Request-Start");
        if (startTimeStr == null) {
            // 如果没有该 header，使用当前时间戳作为默认值
            startTimeStr = String.valueOf(System.nanoTime());
        }

        try {
            long startTime = Long.parseLong(startTimeStr);
            long responseTime = (System.nanoTime() - startTime) / 1000000; // 转换为毫秒
            String date = LocalDate.now().toString();
            int hour = LocalTime.now().getHour();
            redisTemplate.opsForList().rightPush("metrics:response_time:" + date + ":" + hour, String.valueOf(responseTime));
        } catch (NumberFormatException e) {
            // 捕获 NumberFormatException，防止程序崩溃，并记录日志
            logger.error("Failed to parse response time, invalid start time: " + startTimeStr, e);
        }
    }

}
