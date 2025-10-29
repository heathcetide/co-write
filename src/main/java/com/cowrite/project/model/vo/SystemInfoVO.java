package com.cowrite.project.model.vo;

import org.springframework.boot.actuate.health.HealthComponent;

public class SystemInfoVO {

    private String inviteBaseUrl;

    private String fontUrl;

    private HealthComponent healthComponent;

    public String getInviteBaseUrl() {
        return inviteBaseUrl;
    }

    public void setInviteBaseUrl(String inviteBaseUrl) {
        this.inviteBaseUrl = inviteBaseUrl;
    }

    public String getFontUrl() {
        return fontUrl;
    }

    public void setFontUrl(String fontUrl) {
        this.fontUrl = fontUrl;
    }

    public HealthComponent getHealthComponent() {
        return healthComponent;
    }

    public void setHealthComponent(HealthComponent healthComponent) {
        this.healthComponent = healthComponent;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder 静态内部类
    public static class Builder {
        private String inviteBaseUrl;
        private String fontUrl;
        private HealthComponent healthComponent;

        private Builder() {}

        public Builder inviteBaseUrl(String inviteBaseUrl) {
            this.inviteBaseUrl = inviteBaseUrl;
            return this;
        }

        public Builder fontUrl(String fontUrl) {
            this.fontUrl = fontUrl;
            return this;
        }

        public Builder healthComponent(HealthComponent healthComponent) {
            this.healthComponent = healthComponent;
            return this;
        }

        public SystemInfoVO build() {
            SystemInfoVO systemInfoVO = new SystemInfoVO();
            systemInfoVO.inviteBaseUrl = this.inviteBaseUrl;
            systemInfoVO.fontUrl = this.fontUrl;
            systemInfoVO.healthComponent = this.healthComponent;
            return systemInfoVO;
        }
    }
}
