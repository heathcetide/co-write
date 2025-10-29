package com.cowrite.project.model.entity;

import java.util.List;

public class PluginConfig {
    private String pluginName;
    private String pluginType;
    private String version;
    private String author;
    private String description;
    private String documentationUrl;
    private boolean enabled;
    private String createdAt;
    private String updatedAt;
    private List<ApiUrl> apiUrls;
    private String webhookUrl;
    private Authentication authentication;
    private List<Dependency> dependencies;
    private Config config;
    private String auditStatus;
    private List<String> securityWarnings;

    // ---------------- 子类定义 ----------------

    public static class ApiUrl {
        private String name;
        private String url;
        private String method;
        private String description;
        private List<Parameter> parameters;
        private Response response;

        public static class Parameter {
            private String name;
            private String type;
            private boolean required;
            private String description;

            // Getters & Setters
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getType() { return type; }
            public void setType(String type) { this.type = type; }

            public boolean isRequired() { return required; }
            public void setRequired(boolean required) { this.required = required; }

            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            @Override
            public String toString() {
                return "Parameter{" +
                        "name='" + name + '\'' +
                        ", type='" + type + '\'' +
                        ", required=" + required +
                        ", description='" + description + '\'' +
                        '}';
            }
        }

        public static class Response {
            private String description;
            private String suggestion;

            // Getters & Setters
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }

            public String getSuggestion() { return suggestion; }
            public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

            @Override
            public String toString() {
                return "Response{" +
                        "description='" + description + '\'' +
                        ", suggestion='" + suggestion + '\'' +
                        '}';
            }
        }

        // Getters & Setters for ApiUrl
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<Parameter> getParameters() { return parameters; }
        public void setParameters(List<Parameter> parameters) { this.parameters = parameters; }

        public Response getResponse() { return response; }
        public void setResponse(Response response) { this.response = response; }

        @Override
        public String toString() {
            return "ApiUrl{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", method='" + method + '\'' +
                    ", description='" + description + '\'' +
                    ", parameters=" + parameters +
                    ", response=" + response +
                    '}';
        }
    }

    public static class Authentication {
        private String apiKey;
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        @Override
        public String toString() {
            return "Authentication{" +
                    "apiKey='" + apiKey + '\'' +
                    '}';
        }
    }

    public static class Dependency {
        private String pluginName;
        private String version;
        public String getPluginName() { return pluginName; }
        public void setPluginName(String pluginName) { this.pluginName = pluginName; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        @Override
        public String toString() {
            return "Dependency{" +
                    "pluginName='" + pluginName + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }

    public static class Config {
        private String language;
        private int timeout;
        private int retryAttempts;
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        public int getRetryAttempts() { return retryAttempts; }
        public void setRetryAttempts(int retryAttempts) { this.retryAttempts = retryAttempts; }

        @Override
        public String toString() {
            return "Config{" +
                    "language='" + language + '\'' +
                    ", timeout=" + timeout +
                    ", retryAttempts=" + retryAttempts +
                    '}';
        }
    }

    // ---------------- PluginConfig 本体的 Getters & Setters ----------------

    public String getPluginName() { return pluginName; }
    public void setPluginName(String pluginName) { this.pluginName = pluginName; }

    public String getPluginType() { return pluginType; }
    public void setPluginType(String pluginType) { this.pluginType = pluginType; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<ApiUrl> getApiUrls() { return apiUrls; }
    public void setApiUrls(List<ApiUrl> apiUrls) { this.apiUrls = apiUrls; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public Authentication getAuthentication() { return authentication; }
    public void setAuthentication(Authentication authentication) { this.authentication = authentication; }

    public List<Dependency> getDependencies() { return dependencies; }
    public void setDependencies(List<Dependency> dependencies) { this.dependencies = dependencies; }

    public Config getConfig() { return config; }
    public void setConfig(Config config) { this.config = config; }

    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }

    public List<String> getSecurityWarnings() { return securityWarnings; }
    public void setSecurityWarnings(List<String> securityWarnings) { this.securityWarnings = securityWarnings; }

    @Override
    public String toString() {
        return "PluginConfig{" +
                "pluginName='" + pluginName + '\'' +
                ", pluginType='" + pluginType + '\'' +
                ", version='" + version + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", documentationUrl='" + documentationUrl + '\'' +
                ", enabled=" + enabled +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", apiUrls=" + apiUrls +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", authentication=" + authentication +
                ", dependencies=" + dependencies +
                ", config=" + config +
                ", auditStatus='" + auditStatus + '\'' +
                ", securityWarnings=" + securityWarnings +
                '}';
    }
}
