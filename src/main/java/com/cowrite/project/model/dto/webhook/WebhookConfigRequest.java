package com.cowrite.project.model.dto.webhook;

public class WebhookConfigRequest {
    private String webhookUrl; // 客户端接收通知的回调地址

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
}
