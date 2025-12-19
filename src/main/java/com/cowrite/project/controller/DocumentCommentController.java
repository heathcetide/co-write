package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.PageRequest;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.exception.AuthorizationException;
import com.cowrite.project.model.entity.DocumentComment;
import com.cowrite.project.service.DocumentCommentService;
import com.cowrite.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DocumentComment 控制器，提供文档评论相关接口
 * @author Hibiscus-code-generate
 */
@Api(tags = "Document Comment 控制器")
@RestController
@RequestMapping("/api/document-comment")
public class DocumentCommentController {

    private final DocumentCommentService documentCommentService;
    private final UserService userService;

    public DocumentCommentController(DocumentCommentService documentCommentService, UserService userService) {
        this.documentCommentService = documentCommentService;
        this.userService = userService;
    }

    /**
     * 根据文档ID获取评论列表
     */
    @ApiOperation("根据文档ID获取评论列表")
    @GetMapping("/document/{documentId}")
    public ApiResponse<List<DocumentComment>> getCommentsByDocumentId(@PathVariable Long documentId) {
        QueryWrapper<DocumentComment> query = new QueryWrapper<>();
        query.eq("document_id", documentId);
        query.eq("deleted", false);
        query.orderByDesc("created_at");
        return ApiResponse.success(documentCommentService.list(query));
    }

    /**
     * 创建评论
     */
    @ApiOperation("创建评论")
    @PostMapping
    public ApiResponse<DocumentComment> createComment(@RequestBody DocumentComment comment) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        
        comment.setUserId(currentUserId);
        comment.setStatus("ACTIVE");
        boolean saved = documentCommentService.save(comment);
        if (!saved) {
            return ApiResponse.error("创建评论失败");
        }
        return ApiResponse.success(comment);
    }

    /**
     * 更新评论
     */
    @ApiOperation("更新评论")
    @PutMapping("/{id}")
    public ApiResponse<DocumentComment> updateComment(
            @PathVariable Long id,
            @RequestBody DocumentComment comment) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        
        DocumentComment existing = documentCommentService.getById(id);
        if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
            return ApiResponse.error("评论不存在或已被删除");
        }
        
        // 只有评论创建者可以更新
        if (!existing.getUserId().equals(currentUserId)) {
            return ApiResponse.error("无权修改此评论");
        }
        
        comment.setId(id);
        comment.setUserId(existing.getUserId()); // 保持原有userId
        boolean updated = documentCommentService.updateById(comment);
        if (!updated) {
            return ApiResponse.error("更新评论失败");
        }
        return ApiResponse.success(documentCommentService.getById(id));
    }

    /**
     * 更新评论状态（解决/激活）
     */
    @ApiOperation("更新评论状态")
    @PutMapping("/{id}/status")
    public ApiResponse<DocumentComment> updateCommentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        
        DocumentComment comment = documentCommentService.getById(id);
        if (comment == null || Boolean.TRUE.equals(comment.getDeleted())) {
            return ApiResponse.error("评论不存在或已被删除");
        }
        
        // 只有评论创建者或文档所有者可以更新状态
        // TODO: 检查是否是文档所有者
        if (!comment.getUserId().equals(currentUserId)) {
            return ApiResponse.error("无权修改此评论状态");
        }
        
        comment.setStatus(status);
        boolean updated = documentCommentService.updateById(comment);
        if (!updated) {
            return ApiResponse.error("更新评论状态失败");
        }
        return ApiResponse.success(comment);
    }

    /**
     * 删除评论（逻辑删除）
     */
    @ApiOperation("删除评论（逻辑删除）")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        
        DocumentComment comment = documentCommentService.getById(id);
        if (comment == null || Boolean.TRUE.equals(comment.getDeleted())) {
            return ApiResponse.error("评论不存在或已被删除");
        }
        
        // 只有评论创建者可以删除
        if (!comment.getUserId().equals(currentUserId)) {
            return ApiResponse.error("无权删除此评论");
        }
        
        comment.setDeleted(true);
        boolean updated = documentCommentService.updateById(comment);
        if (!updated) {
            return ApiResponse.error("删除评论失败");
        }
        return ApiResponse.success(true);
    }

    /**
     * 根据 ID 获取评论详情
     */
    @ApiOperation("根据ID获取评论详情")
    @GetMapping("/{id}")
    public ApiResponse<DocumentComment> getById(@PathVariable Long id) {
        DocumentComment comment = documentCommentService.getById(id);
        if (comment == null || Boolean.TRUE.equals(comment.getDeleted())) {
            return ApiResponse.error("评论不存在或已被删除");
        }
        return ApiResponse.success(comment);
    }

    /**
     * 分页查询评论列表
     */
    @ApiOperation("分页查询评论列表")
    @PostMapping("/page")
    public ApiResponse<Page<DocumentComment>> getPage(@RequestBody PageRequest pageRequest) {
        Page<DocumentComment> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        QueryWrapper<DocumentComment> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like("content", pageRequest.getKeyword());
        }

        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            wrapper.orderBy(true, "asc".equalsIgnoreCase(pageRequest.getSortOrder()), pageRequest.getSortBy());
        } else {
            wrapper.orderByDesc("created_at");
        }

        return ApiResponse.success(documentCommentService.page(page, wrapper));
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return userService.getCurrentUser().getId();
    }
}
