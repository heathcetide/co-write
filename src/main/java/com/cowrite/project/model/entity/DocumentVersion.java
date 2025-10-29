package com.cowrite.project.model.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * DocumentVersion Mongo Entity
 *
 * @author heathcetide
 */
@Document(collection = "document_versions")
public class DocumentVersion {

    @Id
    private String id;

    private Long documentId;

    private Integer version;

    private String title;

    private String content;

    private Long editorId;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getEditorId() {
        return editorId;
    }

    public void setEditorId(Long editorId) {
        this.editorId = editorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "DocumentVersion{" +
                "id='" + id + '\'' +
                ", documentId=" + documentId +
                ", version=" + version +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", editorId=" + editorId +
                ", createdAt=" + createdAt +
                '}';
    }
}