package com.cowrite.project.model.dto.webhook;


import java.time.LocalDateTime;

    /**
     * 插件更新通知DTO
     */
    public class PluginUpdateNotification {
        private Long pluginId;
        private String pluginName;
        private String oldVersion;
        private String newVersion;
        private String downloadUrl;
        private LocalDateTime updateTime;
        private Boolean isNeedUpdate;

        public Boolean getNeedUpdate() {
            return isNeedUpdate;
        }

        public void setNeedUpdate(Boolean needUpdate) {
            isNeedUpdate = needUpdate;
        }

        public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}


