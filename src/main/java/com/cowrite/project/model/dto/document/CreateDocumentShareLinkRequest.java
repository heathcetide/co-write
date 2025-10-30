package com.cowrite.project.model.dto.document;

import java.time.LocalDateTime;

public class CreateDocumentShareLinkRequest {
    /**
     * 被分享的文档ID
     */
    private Long documentId;

    /**
     * 发送人ID
     */
    private Long shareFromUserId;

    /*
    * 接收人ID
    * */
    private Long shareToUserId;

    /**
     * 分享类型
     */
    private String shareType;

    /**
     * 权限类型
     * 可选值：VIEW（仅查看）、EDIT（可编辑）
     */
    private String permission ;

    /*
    * 分享状态
    * */
    private String status;

    /**
     * 链接过期时间（null 表示永久有效）
     * 格式示例：2024-12-31 23:59:59
     */
    private LocalDateTime expireTime;

    /**
     * 分享备注（附加说明，如“请查看这份文档”）
     */
    private String remark;
    private String accessPassword;
    private String passwordHash;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getShareFromUserId() {
        return shareFromUserId;
    }

    public void setShareFromUserId(Long shareFromUserId) {
        this.shareFromUserId = shareFromUserId;
    }

    public Long getShareToUserId() {
        return shareToUserId;
    }

    public void setShareToUserId(Long shareToUserId) {
        this.shareToUserId = shareToUserId;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getAccessPassword() { return accessPassword; }
    public void setAccessPassword(String accessPassword) { this.accessPassword = accessPassword; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
