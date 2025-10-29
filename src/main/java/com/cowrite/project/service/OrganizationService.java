package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.common.anno.Loggable;
import com.cowrite.project.common.enums.LogType;
import com.cowrite.project.model.entity.Organization;
import com.cowrite.project.model.entity.User;
import com.cowrite.project.model.vo.QuickOrganizationVO;

import java.util.List;

/**
 * OrganizationService
 *
 * @author dont-cry-522
 */
public interface OrganizationService extends IService<Organization> {

    /**
     * 根据用户获取其参与的组织
     */
    List<Organization> getOrganizationsByUser(User currentUser);

    /**
     * 获取组织成员列表
     */
    List<User> getMembersByOrganizationId(Long organizationId);

    /**
     * 设置组织成员角色
     */

    Boolean setMemberRole(Long organizationId, Long userId, String role);

    /**
     * 移除组织成员
     */
    Boolean removeMember(Long organizationId, Long userId);

    /**
     * 切换当前组织
     */
    void switchCurrentOrganization(User currentUser, Long organizationId);
    /**
     * 创建组织
     */
    Organization createOrganization(Organization organization, User creator);

    /*
    * 快速获取用户的全部组织
    * */

    List<QuickOrganizationVO> getOrganizationListQuickly(User currentUser);
}
