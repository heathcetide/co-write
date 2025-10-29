package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Plugin 实体类
 * @author Hibiscus-code-generate
 */
@TableName("hib_plugins")
public class Plugin extends BaseEntity implements Serializable, Cloneable  {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 插件名称
     */
    @TableField("plugin_name")
    private String pluginName;

    /**
     * 插件类型（如：文本生成、图像识别等）
     */
    @TableField("plugin_type")
    private String pluginType;

    /**
     * 插件描述
     */
    @TableField("description")
    private String description;

    /**
     * 插件版本
     */
    @TableField("version")
    private String version;

    /**
     * 插件的 Webhook 回调地址
     */
    @TableField("webhook_url")
    private String webhookUrl;

    /**
     * 插件API地址
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 插件文档地址
     */
    @TableField("documentation_url")
    private String documentationUrl;

    /**
     * 插件作者
     */
    @TableField("author")
    private String author;

    /**
     * 源代码仓库地址
     */
    @TableField("repository_url")
    private String repositoryUrl;

    /**
     * 插件标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 最后验证时间
     */
    @TableField("last_validated_at")
    private LocalDateTime lastValidatedAt;

    /**
     * 安装次数
     */
    @TableField("install_count")
    private Integer installCount;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 评分
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 插件文件存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 插件状态（PENDING/VALIDATED/REJECTED/ACTIVE）
     */
    @TableField("pl_status")
    private String plStatus;

    /**
     * 插件配置数据
     */
    @TableField("config_data")
    private String configData;

    /**
     * 插件是否启用
     */
    @TableField("enabled")
    private Boolean enabled;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }
    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    public String getDocumentationUrl() {
        return documentationUrl;
    }

    public void setDocumentationUrl(String documentationUrl) {
        this.documentationUrl = documentationUrl;
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
    public LocalDateTime getLastValidatedAt() {
        return lastValidatedAt;
    }

    public void setLastValidatedAt(LocalDateTime lastValidatedAt) {
        this.lastValidatedAt = lastValidatedAt;
    }
    public Integer getInstallCount() {
        return installCount;
    }

    public void setInstallCount(Integer installCount) {
        this.installCount = installCount;
    }
    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPlStatus() {
        return plStatus;
    }

    public void setPlStatus(String plStatus) {
        this.plStatus = plStatus;
    }

    public String getConfigData() {
        return configData;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
    }
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Plugin plugin;

        public Builder() {
            this.plugin = new Plugin();
        }

        public Builder id(Long id) {
            this.plugin.setId(id);
            return this;
        }

        public Builder pluginName(String pluginName) {
            this.plugin.setPluginName(pluginName);
            return this;
        }

        public Builder pluginType(String pluginType) {
            this.plugin.setPluginType(pluginType);
            return this;
        }

        public Builder description(String description) {
            this.plugin.setDescription(description);
            return this;
        }

        public Builder version(String version) {
            this.plugin.setVersion(version);
            return this;
        }

        public Builder webhookUrl(String webhookUrl) {
            this.plugin.setWebhookUrl(webhookUrl);
            return this;
        }

        public Builder apiUrl(String apiUrl) {
            this.plugin.setApiUrl(apiUrl);
            return this;
        }

        public Builder documentationUrl(String documentationUrl) {
            this.plugin.setDocumentationUrl(documentationUrl);
            return this;
        }

        public Builder author(String author) {
            this.plugin.setAuthor(author);
            return this;
        }

        public Builder repositoryUrl(String repositoryUrl) {
            this.plugin.setRepositoryUrl(repositoryUrl);
            return this;
        }

        public Builder tags(String tags) {
            this.plugin.setTags(tags);
            return this;
        }

        public Builder lastValidatedAt(LocalDateTime lastValidatedAt) {
            this.plugin.setLastValidatedAt(lastValidatedAt);
            return this;
        }

        public Builder installCount(Integer installCount) {
            this.plugin.setInstallCount(installCount);
            return this;
        }

        public Builder downloadCount(Integer downloadCount) {
            this.plugin.setDownloadCount(downloadCount);
            return this;
        }

        public Builder rating(BigDecimal rating) {
            this.plugin.setRating(rating);
            return this;
        }

        public Builder filePath(String filePath) {
            this.plugin.setFilePath(filePath);
            return this;
        }

        public Builder status(String status) {
            this.plugin.setPlStatus(status);
            return this;
        }

        public Builder configData(String configData) {
            this.plugin.setConfigData(configData);
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.plugin.setEnabled(enabled);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.plugin.setCreatedAt(createdAt);
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.plugin.setUpdatedAt(updatedAt);
            return this;
        }

        public Builder deleted(Boolean deleted) {
            this.plugin.setDeleted(deleted);
            return this;
        }

        public Plugin build() {
            return this.plugin;
        }
    }

    @Override
    public Plugin clone() {
        try {
            return (Plugin) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Failed to clone User object", e);
        }
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "id=" + id +
                ", pluginName='" + pluginName + '\'' +
                ", pluginType='" + pluginType + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", documentationUrl='" + documentationUrl + '\'' +
                ", author='" + author + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", tags='" + tags + '\'' +
                ", lastValidatedAt=" + lastValidatedAt +
                ", installCount=" + installCount +
                ", downloadCount=" + downloadCount +
                ", rating=" + rating +
                ", filePath='" + filePath + '\'' +
                ", plStatus='" + plStatus + '\'' +
                ", configData='" + configData + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}