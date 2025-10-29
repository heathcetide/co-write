package com.cowrite.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import java.net.URI;
import static org.springframework.web.servlet.function.RouterFunctions.route;

/**
 * 灰度发布配置
 *
 * @author heathcetide
 */
@Configuration
@ConditionalOnProperty(name = "cowrite.feature.canary", havingValue = "true")
public class CanaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CanaryConfig.class);

    @Value("${cowrite.feature.canary-header:X-User-ID}")
    private String canaryHeader;

    @Value("${cowrite.feature.canary-rate:10}")
    private int canaryRate;

    @Bean
    public RouterFunction<ServerResponse> canaryRouter() {
        return route()
                .GET("/api/**", this::handleCanary)
                .POST("/api/**", this::handleCanary)
                .PUT("/api/**", this::handleCanary)
                .DELETE("/api/**", this::handleCanary)
                .build();
    }

    private ServerResponse handleCanary(ServerRequest request) {
        String userId = request.headers().firstHeader(canaryHeader);
        String path = request.path();

        if (userId != null && isCanaryUser(userId)) {
            log.info("用户 [{}] 命中灰度策略，重定向到新版路径 /api/v2{}", userId, path);
            return ServerResponse.status(HttpStatus.PERMANENT_REDIRECT)
                    .location(URI.create("/api/v2" + path))
                    .build();
        }

        return ServerResponse.ok().build();
    }

    private boolean isCanaryUser(String userId) {
        int hash = Math.abs(userId.hashCode() % 100);
        return hash < canaryRate;
    }
}