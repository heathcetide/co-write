package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cowrite.project.model.entity.DocumentPermission;
import com.cowrite.project.service.DocumentPermissionService;
import com.cowrite.project.service.PermissionEvaluatorService;
import org.springframework.stereotype.Service;

@Service
public class PermissionEvaluatorServiceImpl implements PermissionEvaluatorService {

    private final DocumentPermissionService docPermService;

    public PermissionEvaluatorServiceImpl(DocumentPermissionService docPermService) {
        this.docPermService = docPermService;
    }

    private String findPermission(String userId, String docId) {
        DocumentPermission p = docPermService.getOne(new QueryWrapper<DocumentPermission>()
                .eq("document_id", docId)
                .eq("user_id", userId)
                .last("limit 1"));
        return p == null ? "VIEW" : p.getPermission();
    }

    @Override
    public boolean canView(String userId, String docId) {
        String perm = findPermission(userId, docId);
        return perm.equals("VIEW") || perm.equals("COMMENT") || perm.equals("EDIT") || perm.equals("ADMIN");
    }

    @Override
    public boolean canComment(String userId, String docId) {
        String perm = findPermission(userId, docId);
        return perm.equals("COMMENT") || perm.equals("EDIT") || perm.equals("ADMIN");
    }

    @Override
    public boolean canEdit(String userId, String docId) {
        String perm = findPermission(userId, docId);
        return perm.equals("EDIT") || perm.equals("ADMIN");
    }

    @Override
    public boolean canAdmin(String userId, String docId) {
        String perm = findPermission(userId, docId);
        return perm.equals("ADMIN");
    }

    @Override
    public boolean isExportDisabled(String userId, String docId) {
        DocumentPermission p = docPermService.getOne(new QueryWrapper<DocumentPermission>()
                .eq("document_id", docId)
                .eq("user_id", userId)
                .last("limit 1"));
        if (p == null) return true; // 无权限默认禁导出
        return p.getDisableExport() != null && p.getDisableExport();
    }
}


