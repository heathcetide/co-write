package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.dto.document.CreateDocumentShareLinkRequest;
import com.cowrite.project.model.entity.DocumentShare;
import com.cowrite.project.model.entity.OrganizationInvite;
import com.cowrite.project.model.vo.DocumentShareVO;
import com.cowrite.project.model.vo.InviteResponseVO;
import com.cowrite.project.model.vo.OrganizationInviteVO;
import com.hibiscus.signal.spring.anno.SignalEmitter;

/**
 * DocumentShare 服务接口
 * @author Hibiscus-code-generate
 */
public interface DocumentShareService extends IService<DocumentShare> {
    /**
     * 创建邀请码
     * @param request 封装后的 DocumentShare 实例
     * @return 邀请码封装好后的VO
     */
    @SignalEmitter("invite.create")
    InviteResponseVO createShareLink(Long id, CreateDocumentShareLinkRequest request);


    /**
     * 根据邀请码获取邀请信息
     * @param code 邀请码
     * @return 邀请信息
     */
    DocumentShare getByCode(String code);

    /**
     * 验证邀请码
     * @param code 邀请码
     * @return 邀请信息
     */
    public DocumentShareVO validateInviteCode(String code);


}
