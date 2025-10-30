package com.cowrite.project.service;

public interface PermissionEvaluatorService {
    enum PermissionLevel { VIEW, COMMENT, EDIT, ADMIN }

    boolean canView(String userId, String docId);
    boolean canComment(String userId, String docId);
    boolean canEdit(String userId, String docId);
    boolean canAdmin(String userId, String docId);
    boolean isExportDisabled(String userId, String docId);
}


