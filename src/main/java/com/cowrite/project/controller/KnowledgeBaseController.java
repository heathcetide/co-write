package com.cowrite.project.controller;

import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.constants.KnowledgeBaseConstants;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.exception.AuthorizationException;
import com.cowrite.project.model.entity.KnowledgeBase;
import com.cowrite.project.model.vo.OrgKnowledgeBaseVO;
import com.cowrite.project.service.KnowledgeBaseService;
import com.cowrite.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * KnowledgeBase 控制器，提供基础增删改查接口
 * 该控制器处理知识库相关的所有 API 请求。
 * @author Hibiscus-code-generate
 */
@Api(tags = "Knowledge Base 控制器")
@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    /**
     * Logger
     */

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);

    /**
     * KnowledgeBase Service
     */

    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * User Service
     */
    private final UserService userService;
    public KnowledgeBaseController(KnowledgeBaseService knowledgeBaseService, UserService userService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.userService = userService;
    }

    /**
     *查询当前组织的知识库列表
     */
    @ApiOperation("查询当前组织的知识库列表")
    @GetMapping("/getOrganizationKnowledgeBases/{id}")
    public ApiResponse<List<OrgKnowledgeBaseVO>> getOrganizationKnowledgeBases(@PathVariable("id") Long organizationId) {
        if (organizationId == null){
            System.out.println("组织ID不能为空");
            return ApiResponse.error("组织ID不能为空");
        }
        return ApiResponse.success(knowledgeBaseService.getOrganizationKnowledgeBases(organizationId));
    }

    /**
     * 创建个人知识库
     */
    @ApiOperation("当前登录用户创建个人知识库")
    @PostMapping("/createPersonal")
    public ApiResponse<KnowledgeBase> createPersonalKnowledgeBase(@RequestBody KnowledgeBase knowledgeBase) {
        // 1. 获取当前登录用户ID，假设通过安全上下文获取
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        // 2. 设置ownerId和organizationId为null（个人知识库）
        knowledgeBase.setOwnerId(currentUserId);
        knowledgeBase.setOrganizationId(null);
        knowledgeBase.setCoverUrl(knowledgeBase.getCoverUrl() == null ? KnowledgeBaseConstants.DEFAULT_KNOWLEDGE_BASE_COVER_URL : knowledgeBase.getCoverUrl());
        // 3. 调用Service保存
        return ApiResponse.success(knowledgeBaseService.saveKnowledgeBase(knowledgeBase));
    }

    /**
     * 获取当前用户的个人知识库
     */
    @ApiOperation("获取当前用户的个人知识库")
    @GetMapping("/getPersonal")
    public ApiResponse<List<KnowledgeBase>> getPersonalKnowledgeBase() {
        // 1. 获取当前登录用户ID，假设通过安全上下文获取
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        // 2. 调用Service获取
        List<KnowledgeBase> knowledgeBase = knowledgeBaseService.getKnowledgeBaseByOwnerId(currentUserId);
        return ApiResponse.success(knowledgeBase);
    }

    /**
     * 更新知识库信息
     */
    @ApiOperation("更新知识库信息")
    @PostMapping("/update")
    public ApiResponse<KnowledgeBase> updateKnowledgeBase(@RequestBody KnowledgeBase knowledgeBase) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new AuthorizationException("当前用户未登录");
        }
        knowledgeBase.setOwnerId(currentUserId);
        return ApiResponse.success(knowledgeBaseService.updateKnowledgeBase(knowledgeBase));
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return userService.getCurrentUser().getId();
    }
}