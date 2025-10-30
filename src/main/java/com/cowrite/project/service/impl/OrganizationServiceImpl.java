package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.common.anno.Loggable;
import com.cowrite.project.common.enums.LogType;
import com.cowrite.project.mapper.OrganizationMapper;
import com.cowrite.project.mapper.OrganizationMemberMapper;
import com.cowrite.project.mapper.UserMapper;
import com.cowrite.project.model.entity.Organization;
import com.cowrite.project.model.entity.OrganizationMember;
import com.cowrite.project.model.entity.User;
import com.cowrite.project.model.vo.QuickOrganizationVO;
import com.cowrite.project.service.OrganizationService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    private final OrganizationMemberMapper organizationMemberMapper;

    private final UserMapper userMapper;


    public OrganizationServiceImpl(OrganizationMemberMapper organizationMemberMapper, UserMapper userMapper) {
        this.organizationMemberMapper = organizationMemberMapper;
        this.userMapper = userMapper;
    }



    /**
     * 用户获取已参与的组织
     */
    @Override
    public List<Organization> getOrganizationsByUser(User currentUser) {
        return super.list(new LambdaQueryWrapper<Organization>().in(
                Organization::getId,
                organizationMemberMapper.selectList(
                         new LambdaQueryWrapper<OrganizationMember>()
                         .eq(OrganizationMember::getUserId, currentUser.getId()))
                        .stream()
                        .map(OrganizationMember::getOrganizationId)
                        .collect(Collectors.toList())
        ));
    }

    /**
     * 获取组织成员列表
     */
    @Override
    public List<User> getMembersByOrganizationId(Long organizationId) {
        List<Long> userIds = organizationMemberMapper.selectList(
                        new LambdaQueryWrapper<OrganizationMember>()
                                .eq(OrganizationMember::getOrganizationId, organizationId))
                .stream()
                .map(OrganizationMember::getUserId)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userMapper.selectBatchIds(userIds);
    }

    /**
     * 设置组织成员角色
     */
    @Override
    @Loggable(type = LogType.ORG_MODULE, value = "设置组织成员角色")
    public Boolean setMemberRole(Long organizationId, Long userId, String role) {
        OrganizationMember organizationMember = organizationMemberMapper.selectOne(
                new LambdaQueryWrapper<OrganizationMember>()
                        .eq(OrganizationMember::getOrganizationId, organizationId)
                        .eq(OrganizationMember::getUserId, userId)
        );
        if (organizationMember == null) {
            return false;
        }
        organizationMember.setRole(role);
        return organizationMemberMapper.updateById(organizationMember) > 0;
    }

    /**
     * 移除组织成员
     */
    @Override
    @Loggable(type = LogType.ORG_MODULE, value = "移除组织成员")
    public Boolean removeMember(Long organizationId, Long userId) {
        OrganizationMember organizationMember = organizationMemberMapper.selectOne(
                new LambdaQueryWrapper<OrganizationMember>()
                        .eq(OrganizationMember::getOrganizationId, organizationId)
                        .eq(OrganizationMember::getUserId, userId)
        );
        if (organizationMember == null) {
            return false;
        }
        return organizationMemberMapper.deleteById(organizationMember.getId()) > 0;
    }

    /**
     * 切换当前选中的组织
     */
    @Override
    public void switchCurrentOrganization(User currentUser, Long organizationId) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getCurrentOrganizationId, organizationId);
        userMapper.update(currentUser, updateWrapper);
    }
    /**
     * 创建组织
     */
    @Override
    @Transactional
    @Loggable(type = LogType.ORG_MODULE, value = "创建组织")
    public Organization createOrganization(Organization organization, User creator) {
        // 保存组织信息
        this.save(organization);

        // 创建组织成员记录，创建者作为OWNER
        OrganizationMember member = new OrganizationMember();
        member.setOrganizationId(organization.getId());
        member.setUserId(creator.getId());
        member.setRole("OWNER");
        organizationMemberMapper.insert(member);

        // 更新组织当前成员数量
        organization.setCurrentMembers(1L);
        this.updateById(organization);

        return organization;
    }

    /**
     * 快速查询用户加入的全部组织
     * */
    @Override
    public List<QuickOrganizationVO> getOrganizationListQuickly(User currentUser) {
        List<OrganizationMember> organizationMembers = organizationMemberMapper.selectList(
                new LambdaQueryWrapper<OrganizationMember>()
                        .eq(OrganizationMember::getUserId, currentUser.getId())
        );
        if (organizationMembers != null && !organizationMembers.isEmpty()) {
            return organizationMembers.stream()
                    .map(organizationMember -> {
                        QuickOrganizationVO quickOrganizationVO = new QuickOrganizationVO();
                        quickOrganizationVO.setId(organizationMember.getOrganizationId());
                        Organization organization = super.getById(organizationMember.getOrganizationId());
                        quickOrganizationVO.setName(organization.getName());
                        quickOrganizationVO.setCurrentMembers(String.valueOf(organization.getCurrentMembers()));
                        return quickOrganizationVO;
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }
}
