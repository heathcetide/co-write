package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.dto.document.CreateDocumentShareLinkRequest;
import com.cowrite.project.model.entity.Document;
import com.cowrite.project.model.entity.DocumentShare;
import com.cowrite.project.model.entity.DocumentVersion;
import com.cowrite.project.model.vo.DocumentShareVO;

import java.util.List;

/**
 * Document 服务接口
 * @author Hibiscus-code-generate
 */
public interface DocumentService extends IService<Document> {

    List<Document> listDocumentByKnowledgeBase(Long knowledgeBaseId, Long currentUserId);

    DocumentVersion getLatestContent(Long id);

    void saveNewVersion(Long documentId, String content, String title, Long editorId, String operationJson);

    DocumentVersion getVersionById(String versionId);

    List<DocumentVersion> getVersions(Long id);

}
