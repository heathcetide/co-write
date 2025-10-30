package com.cowrite.project.service.impl;
import com.cowrite.project.common.anno.Loggable;
import com.cowrite.project.common.enums.LogType;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.config.ServerConfig;
import com.cowrite.project.exception.BusinessException;
import com.cowrite.project.mapper.DocumentShareMapper;
import com.cowrite.project.mapper.DocumentVersionRepository;
import com.cowrite.project.model.dto.document.CreateDocumentShareLinkRequest;
import com.cowrite.project.model.entity.DocumentShare;
import com.cowrite.project.model.entity.Organization;
import com.cowrite.project.model.entity.OrganizationInvite;
import com.cowrite.project.model.entity.OrganizationMember;
import com.cowrite.project.model.vo.DocumentShareVO;
import com.cowrite.project.model.vo.InviteResponseVO;
import com.cowrite.project.model.vo.OrganizationInviteVO;
import com.cowrite.project.service.DocumentService;
import com.cowrite.project.service.DocumentShareService;
import com.cowrite.project.service.UserService;
import com.cowrite.project.utils.CodeUtil;
import com.cowrite.project.utils.RedisUtils;
import com.cowrite.project.utils.SnowflakeIdGenerator;
import com.hibiscus.signal.spring.configuration.SignalContextCollector;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.cowrite.project.common.constants.UserEventConstants.EVENT_INTER_MEDIATE_USER;
import static com.cowrite.project.common.enums.ResponseCodeEnum.*;
import static com.cowrite.project.common.enums.ResponseCodeEnum.ALREADY_MEMBER;

@Service
public class DocumentShareServiceImpl extends ServiceImpl<DocumentShareMapper, DocumentShare> implements DocumentShareService {

    private final ServerConfig serverConfig;

    private final RedisUtils redisUtils;

    private final DocumentService documentService;

    private final UserService userService;

    public DocumentShareServiceImpl(RedisUtils redisUtils, ServerConfig serverConfig,
                                    DocumentService documentService, UserService userService) {
        this.redisUtils = redisUtils;
        this.serverConfig = serverConfig;
        this.documentService = documentService;
        this.userService = userService;
    }

    @Override
    @Loggable(type = LogType.CONFIGURATION, value = "创建分享链接")
    public InviteResponseVO createShareLink(Long documentId, CreateDocumentShareLinkRequest request) {
        DocumentShare share = new DocumentShare();
        share.setId(SnowflakeIdGenerator.nextId());
        share.setDocumentId(documentId);
        share.setShareFromUserId(request.getShareFromUserId());
        share.setShareToUserId(request.getShareToUserId());
        share.setPermission(request.getPermission());
        share.setShareType(request.getShareType());
        share.setRemark(request.getRemark());
        share.setStatus(request.getStatus());
        share.setExpireTime(request.getExpireTime());
        share.setPasswordHash(request.getPasswordHash());
        share.setPasswordRetryCount(0);

        // 生成短码
        String shortCode = CodeUtil.encode(share.getId());
        share.setShortCode(shortCode);
        this.save(share); // 保存分享记录

        String baseUrl = serverConfig.getInviteBaseUrl();
        String fullUrl = baseUrl + "/" + shortCode;
        return new InviteResponseVO(share.getShortCode(), fullUrl, fullUrl);
    }

    @Override
    public DocumentShare getByCode(String code) {
        // 解码短码 -> 邀请ID
        Long DocumentShareId = CodeUtil.decode(code);
        DocumentShare documentShare = this.getById(DocumentShareId);

        if (documentShare == null || documentShare.getExpireTime().isBefore(LocalDateTime.now())) {
            return null;
        }
        return documentShare;
    }

    public DocumentShare getDocIdByCode(String code) {
        LambdaQueryWrapper<DocumentShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DocumentShare::getDocumentId, code);
        return getOne(queryWrapper);
    }

    @Override
    public DocumentShareVO validateInviteCode(String code) {
        DocumentShare documentShare = getByCode(code);
        if (documentShare == null){
            throw new BusinessException(NOT_FOUND.code(), "邀请码不存在");
        }

        // 构造返回信息
        DocumentShareVO vo = new DocumentShareVO();
        if (Objects.equals(documentShare.getStatus(), DocumentShareVO.Status.REVOKED.getDescription())){
            vo.setStatus(DocumentShareVO.Status.REVOKED.getDescription());
            vo.setIsValid(false);
            vo.setInvalidReason("邀请码已被撤销");
        }

        if (documentShare.getExpireTime() != null || documentShare.getExpireTime().isBefore(LocalDateTime.now())){
            vo.setStatus(DocumentShareVO.Status.EXPIRED.getDescription());
            vo.setIsValid(false);
            vo.setInvalidReason("邀请码已过期");
        }

        vo.setStatus(DocumentShareVO.Status.ACTIVE.getDescription());
        vo.setIsValid(true);
        vo.setCreatedAt(documentShare.getCreatedAt());
        vo.setId(documentShare.getId());
        vo.setDocumentId(documentShare.getDocumentId());
        vo.setDocumentTitle(documentService.getById(documentShare.getDocumentId()).getTitle());
        vo.setDocumentType(documentShare.getShareType());
        vo.setRemark(documentShare.getRemark());
        vo.setShortCode(documentShare.getShortCode());
        vo.setShareLink(documentShare.getShareLink());
        vo.setPermission(documentShare.getPermission());
        vo.setShareFromUsername(userService.getById(documentShare.getShareFromUserId()).getUsername());
        vo.setShareToUsername(userService.getById(documentShare.getShareToUserId()).getUsername());
        return vo;
    }


}
