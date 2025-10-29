package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.model.dto.documentTemplate.DocumentTemplateDTO;
import com.cowrite.project.model.entity.DocumentTemplate;
import com.cowrite.project.model.dto.documentTemplate.DocumentTemplateMongoDB;
import com.cowrite.project.service.DocumentTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * DocumentTemplate 控制器，提供基础增删改查接口
 * @author Hibiscus-code-generate
 */
@Api(tags = "DocumentTemplate 控制器")
@RestController
@RequestMapping("/api/document_template")
public class DocumentTemplateController {
    @Resource
    private MongoTemplate mongoTemplate;
    private final DocumentTemplateService documentTemplateService;

    public DocumentTemplateController(DocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }


    /**
     * 分页查询文档模板列表
     */
    @ApiOperation("分页查询文档模板列表")
    @GetMapping("/list")
    public ApiResponse<Page<DocumentTemplate>> listTemplates(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String title
    ) {
        QueryWrapper<DocumentTemplate> query = new QueryWrapper<>();
        if (scope != null) {
            query.eq("scope", scope);
        }
        if (title != null && !title.isEmpty()) {
            query.like("title", title);
        }
        query.orderByDesc("id");
        Page<DocumentTemplate> pageResult = documentTemplateService.page(new Page<>(page, size), query);
        return ApiResponse.success(pageResult);
    }

    /**
     * 根据ID获取模板详情
     */
    @ApiOperation("根据ID获取模板详情")
    @GetMapping("/{id}")
    public ApiResponse<DocumentTemplate> getTemplate(@PathVariable Long id) {
        DocumentTemplate template = documentTemplateService.getById(id);
        if (template == null) {
            return ApiResponse.error("模板不存在");
        }
        return ApiResponse.success(template);
    }

    /**
     * 新建模板
     */
    @ApiOperation("新建模板")
    @PostMapping("/create")
    public ApiResponse<DocumentTemplate> createTemplate(@RequestBody DocumentTemplateDTO dto) {
        // 1. 构建MySQL实体（包含content字段）
        DocumentTemplate template = new DocumentTemplate();
        BeanUtils.copyProperties(dto,template);
//        template.setCreatorId(getCurrentUserId());
        template.setCreatorId(1L);
        template.setDeleted(false);

        boolean saved = documentTemplateService.save(template);
        if (!saved) {
            return ApiResponse.error("创建模板失败");
        }

        // 2. 获取MySQL生成的ID（用于关联MongoDB）
        Long templateId = template.getId();
        if (templateId == null) {
            return ApiResponse.error("模板ID生成失败");
        }

        // 3. 保存到MongoDB（同时写入content字段）
        DocumentTemplateMongoDB mongoDoc = new DocumentTemplateMongoDB();
        mongoDoc.setTemplateId(templateId);
        mongoDoc.setContent(dto.getContent());
        mongoDoc.setVersion(1);
        mongoDoc.setUpdatedAt(LocalDateTime.now());
        mongoTemplate.save(mongoDoc);

        return ApiResponse.success(template);
    }

    /**
     * 更新模板
     */
    @ApiOperation("更新模板")
    @PostMapping("/update")
    public ApiResponse<DocumentTemplate> updateTemplate(@RequestBody DocumentTemplateDTO dto) {

        // 1. 校验模板是否存在
        Long templateId = dto.getId();
        if (templateId == null) {
            return ApiResponse.error("模板ID不能为空");
        }
        DocumentTemplate template = documentTemplateService.getById(templateId);
        if (template == null) {
            return ApiResponse.error("模板不存在或已删除");
        }

        // 2. 更新MySQL基础信息
        BeanUtils.copyProperties(dto, template);
        boolean updated = documentTemplateService.updateById(template);
        if (!updated) {
            return ApiResponse.error("更新模板基础信息失败");
        }

        // 3. 处理MongoDB的version递增
        // 3.1 查询该模板当前最大版本
        Query query = new Query(Criteria.where("templateId").is(templateId));
        query.with(Sort.by(Sort.Direction.DESC, "version"));
        query.limit(1);
        DocumentTemplateMongoDB lastVersionMongo = mongoTemplate.findOne(query, DocumentTemplateMongoDB.class);

        // 3.2 计算新版本号（新增时为1，修改时+1）
        int newVersion = 1;
        if (lastVersionMongo != null) {
            newVersion = lastVersionMongo.getVersion() + 1;
        }

        // 3.3 保存新版本到MongoDB
        DocumentTemplateMongoDB newMongoDoc = new DocumentTemplateMongoDB();
        newMongoDoc.setTemplateId(templateId);
        newMongoDoc.setContent(dto.getContent());
        newMongoDoc.setVersion(newVersion); // 版本递增
        newMongoDoc.setUpdatedAt(LocalDateTime.now());
        mongoTemplate.save(newMongoDoc);

        return ApiResponse.success(template);
    }

    /**
     * 删除模板（逻辑删除）
     */
    @ApiOperation("删除模板（逻辑删除）")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteTemplate(@PathVariable Long id) {
        DocumentTemplate template = documentTemplateService.getById(id);
        if (template == null) {
            return ApiResponse.error("模板不存在");
        }
        // 这里假设模板实体有 deleted 字段，或者你也可以直接 removeById
        template.setDeleted(true);
        boolean updated = documentTemplateService.updateById(template);
        if (!updated) {
            return ApiResponse.error("删除模板失败");
        }
        return ApiResponse.success();
    }

    /**
     * 下载模板（Markdown格式）
     */
    /**
     * 下载模板（Markdown格式）
     * 直接返回文件流，不使用ApiResponse包装
     */
    @ApiOperation("下载模板（Markdown格式）")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable Long id) { // 直接返回ResponseEntity
        // 1. 校验模板存在性
        DocumentTemplate template = documentTemplateService.getById(id);
        if (template == null || template.getDeleted()) {
            // 返回错误状态码和消息（不使用ApiResponse）
            return ResponseEntity
                    .badRequest()
                    .body(("模板不存在或已删除").getBytes(StandardCharsets.UTF_8));
        }

        // 2. 查询MongoDB最新版本的content
        Query query = new Query(Criteria.where("templateId").is(id));
        query.with(Sort.by(Sort.Direction.DESC, "version"));
        query.limit(1);
        DocumentTemplateMongoDB latestMongoDoc = mongoTemplate.findOne(query, DocumentTemplateMongoDB.class);

        if (latestMongoDoc == null || latestMongoDoc.getContent() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(("模板内容不存在").getBytes(StandardCharsets.UTF_8));
        }

        // 3. 构建文件名（处理特殊字符）
        String fileName = template.getTitle().replaceAll("[^a-zA-Z0-9_\\-]", "_") + ".md";

        try {
            // 4. 设置响应头（关键：告知浏览器下载文件及文件名）
            HttpHeaders headers = new HttpHeaders();
            // 处理中文文件名（UTF-8编码后转ISO-8859-1，兼容大部分浏览器）
            String encodedFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentType(MediaType.parseMediaType("text/markdown;charset=UTF-8")); // 明确Markdown类型

            // 5. 返回字节流（直接是content的内容）
            byte[] contentBytes = latestMongoDoc.getContent().getBytes(StandardCharsets.UTF_8);
            return new ResponseEntity<>(contentBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(("下载模板失败：" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * 应用模板：返回最新版本 content
     * 直接返回纯文本 Markdown，不做包装
     */
    @ApiOperation("应用模板：获取模板内容")
    @GetMapping("/apply/{id}")
    public ResponseEntity<String> applyTemplate(@PathVariable Long id) {
        // 1. 校验模板是否存在
        DocumentTemplate template = documentTemplateService.getById(id);
        if (template == null || template.getDeleted()) {
            return ResponseEntity.badRequest().body("模板不存在或已删除");
        }

        // 2. 取 MongoDB 中最新版本的 content
        Query query = new Query(Criteria.where("templateId").is(id))
                .with(Sort.by(Sort.Direction.DESC, "version"))
                .limit(1);
        DocumentTemplateMongoDB mongoDoc = mongoTemplate.findOne(query, DocumentTemplateMongoDB.class);

        if (mongoDoc == null || mongoDoc.getContent() == null) {
            return ResponseEntity.badRequest().body("模板内容缺失");
        }

        // 3. 直接返回 Markdown 原文
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN) // 也可改为 text/markdown
                .body(mongoDoc.getContent());
    }
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return AuthContext.getCurrentUser().getId();
    }
}
