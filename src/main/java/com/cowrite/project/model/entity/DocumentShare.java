package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档转发记录实体类
 * 对应数据库表：hib_document_share
 * @author hezihao
 */
@TableName("hib_document_share")
public class DocumentShare extends BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 被转发的文档ID（关联hib_document.id）
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 转发人ID（谁转发的）
     */
    @TableField("share_from_user_id")
    private Long shareFromUserId;

    /**
     * 接收人ID（转发给谁）
     */
    @TableField("share_to_user_id")
    private Long shareToUserId;

    /**
     * 转发类型（DIRECT：普通文档转发；LINK：写作文档转发）
     */
    @TableField("share_type")
    private String shareType;

    /**
    * 文档分享短码（可转为二维码链接）
    */
    @TableField("short_code")
    private String shortCode;

    /**
     * 转发链接
     */
    @TableField("share_link")
    private String shareLink;

    /**
     * 链接过期时间（NULL表示永久有效）
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 权限（VIEW：仅查看；EDIT：可编辑）
     */
    @TableField("permission")
    private String permission;

    /**
     * 转发状态（ACTIVE：有效；EXPIRED：已过期；REVOKED：已撤销）
     */
    @TableField("status")
    private String status;

    /**
     * 转发备注（如"请查看这个文档"）
     */
    @TableField("remark")
    private String remark;

    /**
     * 外链访问口令（可选，NULL表示无口令）
     */
    @TableField("access_password")
    private String accessPassword;

    /**
     * 口令哈希（存储加密后的口令）
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 口令错误重试次数（默认0）
     */
    @TableField("password_retry_count")
    private Integer passwordRetryCount = 0;

    /**
     * 口令锁定时间（输错多次后锁定）
     */
    @TableField("password_locked_until")
    private LocalDateTime passwordLockedUntil;

    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

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

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
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

    public String getAccessPassword() { return accessPassword; }
    public void setAccessPassword(String accessPassword) { this.accessPassword = accessPassword; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Integer getPasswordRetryCount() { return passwordRetryCount; }
    public void setPasswordRetryCount(Integer passwordRetryCount) { this.passwordRetryCount = passwordRetryCount; }
    public LocalDateTime getPasswordLockedUntil() { return passwordLockedUntil; }
    public void setPasswordLockedUntil(LocalDateTime passwordLockedUntil) { this.passwordLockedUntil = passwordLockedUntil; }

    @Override
    public DocumentShare clone() {
        try {
            return (DocumentShare) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Failed to clone User object", e);
        }
    }

    @Override
    public String toString() {
        return "DocumentShare{" +
                "id=" + id +
                ", documentId=" + documentId +
                ", shareFromUserId=" + shareFromUserId +
                ", shareToUserId=" + shareToUserId +
                ", shareType='" + shareType + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", shareLink='" + shareLink + '\'' +
                ", expireTime=" + expireTime +
                ", permission='" + permission + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
