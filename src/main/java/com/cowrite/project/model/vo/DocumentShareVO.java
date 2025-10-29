package com.cowrite.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DocumentShareVO implements Serializable {
    private Long id;                    // 分享记录ID
    private Long documentId;            // 文档ID
    private String documentTitle;       // 文档标题
    private String documentType;        // 文档类型
    private String shareFromUsername;   // 分享人用户名
    private String shareToUsername;     // 接收人用户名
    private ShareType shareType;        // 分享类型
    private String shortCode;           // 短码
    private String shareLink;           // 分享链接
    private String permission;          // 权限级别
    private String status;              // 分享状态
    private String remark;              // 分享备注

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;   // 过期时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;    // 创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;    // 更新时间

    private Boolean isValid;            // 是否有效（计算字段）
    private String invalidReason;       // 无效原因（如果无效）

    // 分享类型枚举
    public enum ShareType {
        DIRECT("普通文档转发"),
        LINK("写作文档转发");

        private final String description;

        ShareType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 分享状态枚举
    public enum Status {
        ACTIVE("有效"),
        EXPIRED("已过期"),
        REVOKED("已撤销");

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 权限级别枚举
    public enum Permission {
        VIEW("仅查看"),
        EDIT("可编辑");

        private final String description;

        Permission(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getShareFromUsername() {
        return shareFromUsername;
    }

    public void setShareFromUsername(String shareFromUsername) {
        this.shareFromUsername = shareFromUsername;
    }

    public String getShareToUsername() {
        return shareToUsername;
    }

    public void setShareToUsername(String shareToUsername) {
        this.shareToUsername = shareToUsername;
    }

    public ShareType getShareType() {
        return shareType;
    }

    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean valid) {
        this.isValid = valid;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }
}
