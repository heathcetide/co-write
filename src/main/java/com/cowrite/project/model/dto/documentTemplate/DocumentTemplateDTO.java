package com.cowrite.project.model.dto.documentTemplate;

public class DocumentTemplateDTO {
    /**
     * 创建模板的DTO，用户仅需传递这三个字段
     */
    private Long id;
    private String title;        // 模板标题
    private String description;  // 模板描述
    private String content;      // Markdown内容（同时存MySQL和MongoDB）
    private String scope;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "DocumentTemplateDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}