package com.cowrite.project.common.risk.model;

import com.cowrite.project.common.risk.enums.RiskAction;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class RiskRule {

    /**
     * 区分不同的规则ID
     */
    private String id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则场景
     */
    private String scenario;

    /**
     * 规则条件
     */
    private String condition;

    /**
     * 规则分数
     */
    private double score;

    /**
     * 规则动作
     */
    private RiskAction action;

    /**
     * 规则持续时间
     */
    private long limitDuration;

    /**
     * 规则是否启用
     */
    private boolean enabled;

    // SpEL解析器实例
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 规则评估方法
     * @param context 风险上下文
     * @return 是否满足规则
     */
    public boolean evaluate(RiskContext context) {
        try {
            // 1. 解析规则条件表达式
            Expression expression = parser.parseExpression(this.condition); // 使用当前规则的condition

            // 2. 创建SpEL评估上下文
            StandardEvaluationContext evalContext = new StandardEvaluationContext();
            evalContext.setVariable("context", context);  // 将RiskContext对象传入
            evalContext.setVariable("properties", context.getProperties());  // 如果需要使用额外的属性

            // 3. 评估规则条件表达式
            Boolean result = expression.getValue(evalContext, Boolean.class);

            return Boolean.TRUE.equals(result);  // 如果评估结果为true，则规则条件成立
        } catch (Exception e) {
            // 捕获异常，说明评估失败，返回false
            System.out.println("规则评估失败: " + e.getMessage());
            return false;
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public RiskAction getAction() {
        return action;
    }

    public void setAction(RiskAction action) {
        this.action = action;
    }

    public long getLimitDuration() {
        return limitDuration;
    }

    public void setLimitDuration(long limitDuration) {
        this.limitDuration = limitDuration;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
} 