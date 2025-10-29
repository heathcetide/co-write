package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.model.entity.KnowledgeBase;
import com.cowrite.project.model.vo.OrgKnowledgeBaseVO;

import java.util.List;

public interface KnowledgeBaseService extends IService<KnowledgeBase> {

    /**
     * 保存知识库
     */
    KnowledgeBase saveKnowledgeBase(KnowledgeBase knowledgeBase);

    /**
     * 根据用户ID获取知识库
     */
    List<KnowledgeBase> getKnowledgeBaseByOwnerId(Long currentUserId);

    /**
     * 更新知识库
     */
    KnowledgeBase updateKnowledgeBase(KnowledgeBase knowledgeBase);

    /**
     * 查询当前组织的知识库
     * */
    List<OrgKnowledgeBaseVO> getOrganizationKnowledgeBases(Long organizationId);
}
