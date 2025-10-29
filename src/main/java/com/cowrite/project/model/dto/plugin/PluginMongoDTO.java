package com.cowrite.project.model.dto.plugin;

import javax.persistence.Id;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "plugin_version_info") // 集合名称
public class PluginMongoDTO {
    @Id
    private Long pluginId; // 与MySQL中的插件id保持一致
    private String version; // 插件版本号
    private LocalDateTime updateTime; // 更新时间

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
