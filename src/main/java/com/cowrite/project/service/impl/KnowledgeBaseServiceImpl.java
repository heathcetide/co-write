package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.mapper.KnowledgeBaseMapper;
import com.cowrite.project.model.entity.KnowledgeBase;
import com.cowrite.project.model.vo.OrgKnowledgeBaseVO;
import com.cowrite.project.service.KnowledgeBaseService;
import com.hibiscus.signal.spring.configuration.SignalContextCollector;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.cowrite.project.common.constants.UserEventConstants.EVENT_INTER_MEDIATE_USER;

@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper,KnowledgeBase> implements KnowledgeBaseService {

    /**
     * 获取当前组织的知识库
     * */
    @Override
    public List<OrgKnowledgeBaseVO> getOrganizationKnowledgeBases(Long organizationId){
        //查询组织下的所有未删除的知识库
        List<KnowledgeBase> knowledgeBaseList = this.list(new QueryWrapper<KnowledgeBase>()
                .eq("organization_id", organizationId)
                .eq("deleted", false));
        if (knowledgeBaseList.isEmpty()){
            return new ArrayList<>();
        }
        // 初始化一个列表用于存放封装后的VO对象
        List<OrgKnowledgeBaseVO> orgKnowledgeBaseVOList = new ArrayList<>();
        knowledgeBaseList.forEach(knowledgeBase -> {
            OrgKnowledgeBaseVO vo = new OrgKnowledgeBaseVO();
            vo.setId(knowledgeBase.getId());
            vo.setName(knowledgeBase.getName());
            orgKnowledgeBaseVOList.add(vo);
        });
        return orgKnowledgeBaseVOList;
    }

    /**
     * 保存知识库
     */
    @Override
    public KnowledgeBase saveKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.save(knowledgeBase);
        return knowledgeBase;
    }

    /**
     * 获取当前用户的个人知识库
     */
    @Override
    public List<KnowledgeBase> getKnowledgeBaseByOwnerId(Long currentUserId) {
        return this.list(new QueryWrapper<KnowledgeBase>()
                .eq("owner_id", currentUserId)
                .eq("deleted", false));
    }

    /**
     * 更新知识库信息
     */
    @Override
    public KnowledgeBase updateKnowledgeBase(KnowledgeBase knowledgeBase) {
        return this.updateById(knowledgeBase) ? knowledgeBase : null;
    }
}
