package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.PageRequest;
import com.cowrite.project.config.ServerConfig;
import com.cowrite.project.model.dto.document.CreateDocumentShareLinkRequest;
import com.cowrite.project.model.entity.DocumentShare;
import com.cowrite.project.model.entity.OrganizationInvite;
import com.cowrite.project.model.vo.DocumentShareVO;
import com.cowrite.project.model.vo.InviteResponseVO;
import com.cowrite.project.model.vo.OrganizationInviteVO;
import com.cowrite.project.service.DocumentService;
import com.cowrite.project.service.DocumentShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


/**
 * DocumentShare 控制器，提供基础增删改查接口
 *
 * @author Hibiscus-code-generate
 */
@Api(tags = "DocumentShare 控制器")
@RestController
@RequestMapping("/api/document/share")
public class DocumentShareController {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    /**
     * DocumentShareService
     */
    private final DocumentShareService  documentShareService;

    /**
     * ServerConfig
     */
    private final ServerConfig serverConfig;

    public DocumentShareController(DocumentShareService  documentShareService, ServerConfig serverConfig) {
        this.documentShareService = documentShareService;
        this.serverConfig = serverConfig;
    }

    /**
     * 新增 DocumentShare 记录
     * 用于文档分享记录
     * @param entity 新的文档分享实体对象
     * @return 返回是否新增成功的响应
     */
    @PostMapping
    public ApiResponse<Boolean> add(@RequestBody DocumentShare entity) {
        return ApiResponse.success(documentShareService.save(entity));
    }

    /**
     * 更新 DocumentShare 记录
     * 用于更新现有的文档分享记录，必须提供包含 ID 的文档分享实体。
     * @param entity 更新后的文档分享对象（必须包含 ID）
     * @return 返回是否更新成功的响应
     */
    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@RequestBody DocumentShare entity) {
        return ApiResponse.success(documentShareService.updateById(entity));
    }

    /**
     * 删除指定 ID 的 DocumentShare 记录
     * 根据提供的 ID 删除文档分享记录。
     * @param id 要删除的文档分享的主键 ID
     * @return 返回是否删除成功的响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable("id") Integer id) {
        return ApiResponse.success(documentShareService.removeById(id));
    }

    /**
     * 根据 ID 获取 DocumentShare 详情
     * 根据提供的 ID 查询文档分享记录的详细信息。
     * @param id 要查询的文档分享记录的主键 ID
     * @return 返回查询到的文档分享实体对象
     */
    @GetMapping("/{id}")
    public ApiResponse<DocumentShare> getById(@PathVariable("id") Integer id) {
        return ApiResponse.success(documentShareService.getById(id));
    }

    /**
     * 获取所有 DocumentShare 列表（不分页）
     * 返回所有的组织邀请记录，不进行分页。
     * @return 返回所有组织邀请的实体列表
     */
    @GetMapping
    public ApiResponse<List<DocumentShare>> list() {
        return ApiResponse.success(documentShareService.list());
    }

    /**
     * 分页查询 DocumentShare 列表
     * 提供分页查询文档分享记录的功能，支持关键字搜索和排序。
     * @param pageRequest 分页请求对象，包含页码、每页大小、搜索关键词、排序字段等
     * @return 返回分页后的文档分享的列表
     */
    @PostMapping("/page")
    public ApiResponse<Page<DocumentShare>> getPage(@RequestBody PageRequest pageRequest) {
        // 创建分页对象
        Page<DocumentShare> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());

        // 构造查询条件
        QueryWrapper<DocumentShare> wrapper = new QueryWrapper<>();

        // 如果有排序字段，进行排序
        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            wrapper.orderBy(true, "asc".equalsIgnoreCase(pageRequest.getSortOrder()), pageRequest.getSortBy());
        }

        // 返回分页结果
        return ApiResponse.success(documentShareService.page(page, wrapper));
    }

    /**
     * 创建文档分享链接
     */
    @ApiOperation("创建文档分享链接")
    @PostMapping("/{id}/create")
    public ApiResponse<InviteResponseVO> createShareLink(
            @PathVariable Long id,
            @RequestBody CreateDocumentShareLinkRequest request) {
        return ApiResponse.success(documentShareService.createShareLink(id, request));
    }

    /**
     * 验证邀请码
     */
    @ApiOperation("验证邀请码")
    @GetMapping("/validate/{code}")
    public ApiResponse<DocumentShareVO> validateInvite(@PathVariable String code) {
        return ApiResponse.success(documentShareService.validateInviteCode(code));
    }

    /**
     * 跳转到邀请页面
     */
    @ApiOperation("跳转到邀请页面")
    @GetMapping("/link/{code}")
    public ResponseEntity<Object> redirectToInvitePage(@PathVariable String code) {
        DocumentShare documentShare = documentShareService.getByCode(code);
        if (documentShare == null || documentShare.isExpired()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // 构造前端页面地址
        URI redirectUri = URI.create(serverConfig.getFontUrl() + "?code=" + code);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(redirectUri);
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 重定向
    }


}
