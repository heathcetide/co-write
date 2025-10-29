package com.cowrite.project.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * JSON 工具类
 *
 * @author heathcetide
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置 ObjectMapper
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param obj 对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 将 JSON 字符串转换为对象
     *
     * @param json JSON 字符串
     * @param clazz 目标对象的类
     * @param <T> 目标对象的类型
     * @return 目标对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }

    /**
     * 将 JSON 字符串转换为 ObjectNode
     *
     * @param json JSON 字符串
     * @return ObjectNode
     */
    public static ObjectNode fromJsonToObjectNode(String json) {
        try {
            return objectMapper.readValue(json, ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to ObjectNode", e);
        }
    }

    /**
     * 创建一个新的 ObjectNode
     *
     * @return ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * 将对象转换为 ObjectNode
     *
     * @param obj 对象
     * @return ObjectNode
     */
    public static ObjectNode toObjectNode(Object obj) {
        try {
            return objectMapper.convertValue(obj, ObjectNode.class);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to convert object to ObjectNode", e);
        }
    }
}
