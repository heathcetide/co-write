package com.cowrite.project.common.risk.model;

import java.util.Map;

public class RiskContext {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 场景名称
     */
    private String scenario;

    /**
     * 具体行为
     */
    private String action;

    /**
     * 自定义属性，可能包括设备信息、IP 地址、操作系统、浏览器类型
     */
    private Map<String, Object> properties;

    /**
     * 事件发生时间戳
     */
    private long timestamp;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}