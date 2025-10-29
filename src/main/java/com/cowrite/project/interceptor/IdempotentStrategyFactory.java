package com.cowrite.project.interceptor;


public class IdempotentStrategyFactory {
    public static IdempotentStrategy createStrategy(String type) {
        return switch (type) {
            case "default" -> new DefaultIdempotentStrategy();
            case "requestParam" -> new RequestParamIdempotentStrategy();
            case "requestBody" -> new RequestBodyIdempotentStrategy();
            default -> throw new IllegalArgumentException("不支持的幂等性策略类型: " + type);
        };
    }
} 