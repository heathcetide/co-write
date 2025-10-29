package com.cowrite.project.model.dto.documentTemplate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
/**
 * MongoDB存储的模板内容（包含Markdown内容和版本）
 *
 * @author heathcetide
 */
@Document(collection = "document_template_content")
public class DocumentTemplateMongoDB {
    @Id
    private String id; // MongoDB自动生成的唯一ID（与MySQL的template_id关联）
    @Indexed
    private Long templateId; // 关联MySQL中模板的ID（关键关联字段）

    private String content; // Markdown模板内容（核心字段）

    private Integer version; // 版本号

    private LocalDateTime updatedAt; // 内容更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}