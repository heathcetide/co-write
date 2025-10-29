package com.cowrite.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.config.ServerConfig;
import com.cowrite.project.mapper.DocumentMapper;
import com.cowrite.project.mapper.DocumentVersionRepository;
import com.cowrite.project.model.entity.Document;
import com.cowrite.project.model.entity.DocumentVersion;
import com.cowrite.project.service.DocumentService;
import com.cowrite.project.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Document 服务实现类
 * @author Hibiscus-code-generate
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    private final RedisUtils redisUtils;

    private final ServerConfig serverConfig;

    private final DocumentVersionRepository versionRepository;

    public DocumentServiceImpl(RedisUtils redisUtils, ServerConfig serverConfig, DocumentVersionRepository versionRepository) {
        this.redisUtils = redisUtils;
        this.serverConfig = serverConfig;
        this.versionRepository = versionRepository;
    }

    @Override
    public List<Document> listDocumentByKnowledgeBase(Long knowledgeBaseId, Long currentUserId) {
        // 1. 查询符合条件的文档基础信息（不含 title）
        QueryWrapper<Document> query = new QueryWrapper<>();
        query.eq("knowledge_base_id", knowledgeBaseId);
        query.eq("owner_id", currentUserId);
        query.eq("deleted", 0);
        query.orderByAsc("level", "sort_order");

        List<Document> dbDocs = list(query);
        if (dbDocs.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 批量获取 Redis 中的 title 缓存
        List<Long> docIds = dbDocs.stream().map(Document::getId).collect(Collectors.toList());
        List<String> redisKeys = docIds.stream().map(id -> "doc:" + id + ":title").collect(Collectors.toList());
        List<String> cachedTitles = redisUtils.multiGet(redisKeys);

        // 3. 填充 title（缓存优先，不足的回源 DB）
        for (int i = 0; i < dbDocs.size(); i++) {
            Document doc = dbDocs.get(i);
            String cachedTitle = cachedTitles.get(i);

            if (cachedTitle != null) {
                doc.setTitle(cachedTitle);
            } else {
                // 如果缓存没有，使用数据库中已有的 title（你 list 查询时可能已包含）
                // 若你只查了 id，可以单独查 title
                String dbTitle = doc.getTitle();
                if (dbTitle != null) {
                    redisUtils.set("doc:" + doc.getId() + ":title", dbTitle, Duration.ofHours(1));
                }
            }
        }
        return dbDocs;
    }

    public DocumentVersion getLatestVersion(Long documentId) {
        return versionRepository.findTopByDocumentIdOrderByVersionDesc(documentId);
    }
    @Override
    public DocumentVersion getLatestContent(Long docId) {
        String keyPrefix = "doc:" + docId;

        String content = (String) redisUtils.get(keyPrefix + ":content");
        String title = (String) redisUtils.get(keyPrefix + ":title");
        String editorId = (String) redisUtils.get(keyPrefix + ":editor");

        if (StrUtil.isNotBlank(content) && StrUtil.isNotBlank(title)) {
            DocumentVersion version = new DocumentVersion();
            version.setDocumentId(docId);
            version.setContent(content);
            version.setTitle(title);
            version.setEditorId(Long.valueOf(editorId));
            version.setCreatedAt(Instant.now());
            return version;
        }

        // fallback：Redis 没有，再读数据库
        DocumentVersion latest = getLatestVersion(docId);
        if (latest == null) {
            Document doc = getById(docId);
            if (doc == null || doc.getDeleted()) return null;
            latest = BeanUtil.toBean(doc, DocumentVersion.class);
        }

        return latest;
    }

    public void saveNewVersion(Long documentId, String content, String title, Long editorId, String operationJson) {
        String keyPrefix = "doc:" + documentId;

        // 缓存当前内容
        redisUtils.set(keyPrefix + ":content", content);
        redisUtils.set(keyPrefix + ":title", title);
        redisUtils.set(keyPrefix + ":editor", String.valueOf(editorId));

        // 初始化版本号为 1（仅第一次）
        redisUtils.setIfAbsent(keyPrefix + ":version", 1);

        // 每次操作都递增版本号
        Long version = redisUtils.increment(keyPrefix + ":version",1);

        // 操作记录追加
        String queueKey = "queue:doc:" + documentId;
        redisUtils.push(queueKey, operationJson);

        // 设置操作队列过期时间
        redisUtils.expire(queueKey, Duration.ofHours(2));
//        Document document = documentService.getById(documentId);
//        if (document == null) throw new IllegalArgumentException("文档不存在");
//
//        Integer newVersion = document.getVersion() + 1;
//
//        DocumentVersion version = new DocumentVersion();
//        version.setDocumentId(documentId);
//        version.setVersion(newVersion);
//        version.setContent(content);
//        version.setEditorId(editorId);
//        version.setCreatedAt(Instant.now());
//        version.setTitle(title);
//
//        DocumentVersion savedVersion = versionRepository.save(version);
//
//        document.setVersion(newVersion);
//        document.setTitle(title);
//        documentService.updateById(document);
//
//        return savedVersion;
    }

    public DocumentVersion getVersionById(String versionId) {
        return versionRepository.findById(versionId).orElse(null);
    }

    public List<DocumentVersion> getVersions(Long documentId) {
        return versionRepository.findByDocumentIdOrderByVersionDesc(documentId);
    }

}
