package com.cowrite.project.controller;

import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.model.dto.document.CreateDocumentShareLinkRequest;
import com.cowrite.project.model.dto.document.ValidateSharePasswordRequest;
import com.cowrite.project.model.entity.DocumentShare;
import com.cowrite.project.model.vo.DocumentShareVO;
import com.cowrite.project.model.vo.InviteResponseVO;
import com.cowrite.project.service.DocumentShareService;
import com.cowrite.project.service.PasswordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/document-share")
public class DocumentShareController {

    @Resource
    private DocumentShareService documentShareService;

    @Resource
    private PasswordService passwordService;

    @PostMapping("/create")
    public ApiResponse<InviteResponseVO> createShareLink(@RequestBody CreateDocumentShareLinkRequest request) {
        // 如果设置了口令，先加密存储
        if (request.getAccessPassword() != null && !request.getAccessPassword().isEmpty()) {
            String hashedPassword = passwordService.hashPassword(request.getAccessPassword());
            request.setPasswordHash(hashedPassword);
        }
        return ApiResponse.success(documentShareService.createShareLink(request.getDocumentId(), request));
    }

    @PostMapping("/validate-password")
    public ApiResponse<Boolean> validatePassword(@RequestBody ValidateSharePasswordRequest request) {
        DocumentShare share = documentShareService.getByCode(request.getCode());
        if (share == null) {
            return ApiResponse.error("分享链接不存在");
        }

        // 检查是否被锁定
        if (passwordService.isPasswordLocked(share.getPasswordLockedUntil())) {
            return ApiResponse.error("密码输入错误次数过多，请稍后再试");
        }

        // 验证密码
        if (share.getPasswordHash() == null || share.getPasswordHash().isEmpty()) {
            return ApiResponse.success(true); // 无密码
        }

        boolean isValid = passwordService.verifyPassword(request.getPassword(), share.getPasswordHash());
        
        if (isValid) {
            // 重置重试次数
            share.setPasswordRetryCount(0);
            share.setPasswordLockedUntil(null);
            documentShareService.updateById(share);
            return ApiResponse.success(true);
        } else {
            // 增加重试次数
            int retryCount = share.getPasswordRetryCount() + 1;
            share.setPasswordRetryCount(retryCount);
            
            // 超过5次锁定30分钟
            if (retryCount >= 5) {
                share.setPasswordLockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            documentShareService.updateById(share);
            
            return ApiResponse.error("密码错误，剩余重试次数: " + (5 - retryCount));
        }
    }

    @GetMapping("/{code}")
    public ApiResponse<DocumentShareVO> getShareInfo(@PathVariable String code) {
        DocumentShareVO shareVO = documentShareService.validateInviteCode(code);
        return ApiResponse.success(shareVO);
    }
}