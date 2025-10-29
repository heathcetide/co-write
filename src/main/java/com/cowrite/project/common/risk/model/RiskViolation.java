package com.cowrite.project.common.risk.model;

public class RiskViolation {

    /**
     * 违反的规则
     */
    private RiskRule rule;

    /**
     * 违反的规则上下文
     */
    private RiskContext context;

    public RiskViolation(RiskRule rule, RiskContext context) {
        this.rule = rule;
        this.context = context;
    }

    // Getters and setters
    public RiskRule getRule() {
        return rule;
    }

    public void setRule(RiskRule rule) {
        this.rule = rule;
    }

    public RiskContext getContext() {
        return context;
    }

    public void setContext(RiskContext context) {
        this.context = context;
    }
} 