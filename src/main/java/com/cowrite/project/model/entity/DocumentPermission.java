package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DocumentPermission 实体类
 * @author Hibiscus-code-generate
 */
@TableName("hib_document_permission")
public class DocumentPermission extends BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId
    private Long id;

    /**
    * 文档ID
    */
    @TableField("document_id")
    private Long documentId;

    /**
    * 被授权用户ID
    */
    @TableField("user_id")
    private Long userId;

    /**
     * 权限类型（VIEW / EDIT / COMMENT / ADMIN）
     */
    @TableField("permission")
    private String permission;

    /**
     * 是否禁用导出（默认false）
     */
    @TableField("disable_export")
    private Boolean disableExport = false;

    /**
    * 授权人ID
    */
    @TableField("granted_by")
    private Long grantedBy;

    /**
    * 授权时间
    */
    @TableField("granted_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime grantedAt;


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
    public Long getUserId() {
    return userId;
    }

    public void setUserId(Long userId) {
    this.userId = userId;
    }
    public String getPermission() {
    return permission;
    }

    public void setPermission(String permission) {
    this.permission = permission;
    }
    public Long getGrantedBy() {
    return grantedBy;
    }

    public void setGrantedBy(Long grantedBy) {
    this.grantedBy = grantedBy;
    }
    public LocalDateTime getGrantedAt() {
    return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }
    public Boolean getDisableExport() { return disableExport; }
    public void setDisableExport(Boolean disableExport) { this.disableExport = disableExport; }

    @Override
    public DocumentPermission clone() {
        try {
            return (DocumentPermission) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Failed to clone User object", e);
        }
    }

    @Override
    public String toString() {
        return "DocumentPermission{" +
                "id=" + id +
                ", documentId=" + documentId +
                ", userId=" + userId +
                ", permission='" + permission + '\'' +
                ", grantedBy=" + grantedBy +
                ", grantedAt=" + grantedAt +
                '}';
    }
}
