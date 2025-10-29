package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.PageRequest;
import com.cowrite.project.model.dto.organization.CreateOrganizationRequest;
import com.cowrite.project.model.entity.Organization;
import com.cowrite.project.model.entity.User;
import com.cowrite.project.model.vo.QuickOrganizationVO;
import com.cowrite.project.service.OrganizationService;
import com.cowrite.project.service.UserService;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Organization 控制器，提供基础增删改查接口
 * 该控制器处理组织管理相关的所有 API 请求。
 * @author Hibiscus-code-generate
 */
@Api(tags = "Organization 控制器")
@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    /**
     * 日志记录器
     * 用于记录组织管理控制器中的操作日志
     */
    private static final Logger log = LoggerFactory.getLogger(Organization.class);

    /**
     * 组织服务
     */
    private final OrganizationService organizationService;

    /**
     * 用户服务
     */
    private final UserService userService;

    public OrganizationController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    /**
     * 快速查询当前用户的全部组织
     */
    @GetMapping("/quickly")
    @ApiOperation("快速获取用户加入的全部组织")
    public ApiResponse<List<QuickOrganizationVO>> getOrganizationListQuickly(){
        User currentUser = userService.getCurrentUser();
        return ApiResponse.success(organizationService.getOrganizationListQuickly(currentUser));
    }

    /**
     * 查询当前用户拥有的组织
     */
    @GetMapping("/organized")
    @ApiOperation("用户获取已参与的组织")
    public ApiResponse<List<Organization>> getOrganizations(){
        User currentUser = userService.getCurrentUser();
        return ApiResponse.success(organizationService.getOrganizationsByUser(currentUser));
    }

    /**
     * 获取组织成员列表
     */
    @ApiOperation("获取组织成员列表")
    @GetMapping("/{organizationId}/members")
    public ApiResponse<List<User>> getOrganizationMembers(@PathVariable Long organizationId) {
        return ApiResponse.success(organizationService.getMembersByOrganizationId(organizationId));
    }

    @ApiOperation("切换当前组织")
    @PostMapping("/switch")
    public ApiResponse<Boolean> switchOrganization(@RequestParam Long organizationId) {
        organizationService.switchCurrentOrganization(userService.getCurrentUser(), organizationId);
        return ApiResponse.success(true);
    }

    /**
     * 设置组织成员角色
     */
    @ApiOperation("设置组织成员角色")
    @PostMapping("/{organizationId}/member/{userId}/role")
    public ApiResponse<Boolean> setMemberRole(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @RequestParam String role) {
        return ApiResponse.success(organizationService.setMemberRole(organizationId, userId, role));
    }

    /**
     * 移除组织成员
     */
    @ApiOperation("移除组织成员")
    @DeleteMapping("/{organizationId}/member/{userId}")
    public ApiResponse<Boolean> removeMember(
            @PathVariable Long organizationId,
            @PathVariable Long userId) {
        return ApiResponse.success(organizationService.removeMember(organizationId, userId));
    }
    /**
     * 创建组织
     */
    @ApiOperation("创建组织")
    @PostMapping("/create")
    public ApiResponse<Organization> createOrganization(@RequestBody @Valid CreateOrganizationRequest request) {
        User currentUser = userService.getCurrentUser();

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setPublished(request.getPublished());
        organization.setMaxMembers(request.getMaxMembers());
        organization.setOwnerId(currentUser.getId());
        organization.setStatus("active");
        organization.setCurrentMembers(1L);

        Organization createdOrg = organizationService.createOrganization(organization, currentUser);
        return ApiResponse.success(createdOrg);
    }
}
