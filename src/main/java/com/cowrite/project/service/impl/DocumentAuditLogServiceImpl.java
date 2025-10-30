package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.DocumentAuditLogMapper;
import com.cowrite.project.model.entity.DocumentAuditLog;
import com.cowrite.project.service.DocumentAuditLogService;
import org.springframework.stereotype.Service;

@Service
public class DocumentAuditLogServiceImpl extends ServiceImpl<DocumentAuditLogMapper, DocumentAuditLog> implements DocumentAuditLogService {
}


