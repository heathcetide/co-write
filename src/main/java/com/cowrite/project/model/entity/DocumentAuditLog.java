package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("hib_document_audit_log")
public class DocumentAuditLog extends BaseEntity implements Serializable, Cloneable {

    @TableId
    private Long id;

    @TableField("document_id")
    private String documentId;

    @TableField("user_id")
    private String userId;

    @TableField("username")
    private String username;

    @TableField("operation")
    private String operation; // INSERT / DELETE / COMMENT / EXPORT

    @TableField("pos")
    private Integer pos;

    @TableField("length")
    private Integer length;

    @TableField("delta_text")
    private String deltaText; // 变更内容片段（可截断）

    @TableField("ip")
    private String ip;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public Integer getPos() { return pos; }
    public void setPos(Integer pos) { this.pos = pos; }
    public Integer getLength() { return length; }
    public void setLength(Integer length) { this.length = length; }
    public String getDeltaText() { return deltaText; }
    public void setDeltaText(String deltaText) { this.deltaText = deltaText; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


