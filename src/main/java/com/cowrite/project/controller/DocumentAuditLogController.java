package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cowrite.project.model.entity.DocumentAuditLog;
import com.cowrite.project.service.DocumentAuditLogService;
import com.cowrite.project.service.PermissionEvaluatorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
public class DocumentAuditLogController {

    private final DocumentAuditLogService service;
    private final PermissionEvaluatorService permissionEvaluator;

    public DocumentAuditLogController(DocumentAuditLogService service, PermissionEvaluatorService permissionEvaluator) {
        this.service = service;
        this.permissionEvaluator = permissionEvaluator;
    }

    @GetMapping("/document/{docId}")
    public List<DocumentAuditLog> list(@PathVariable String docId) {
        return service.list(new QueryWrapper<DocumentAuditLog>().eq("document_id", docId));
    }

    @GetMapping("/document/{docId}/export")
    public ResponseEntity<byte[]> export(@PathVariable String docId, @RequestParam String userId) {
        // 检查导出权限
        if (permissionEvaluator.isExportDisabled(userId, docId)) {
            return ResponseEntity.status(403).body("导出权限被禁用".getBytes());
        }
        List<DocumentAuditLog> logs = service.list(new QueryWrapper<DocumentAuditLog>().eq("document_id", docId));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String header = "documentId,userId,username,operation,pos,length,deltaText,ip,createdAt\n";
        String body = logs.stream().map(l -> String.join(",",
                quote(l.getDocumentId()),
                quote(l.getUserId()),
                quote(l.getUsername()),
                quote(l.getOperation()),
                String.valueOf(nullToZero(l.getPos())),
                String.valueOf(nullToZero(l.getLength())),
                quote(safe(l.getDeltaText())),
                quote(safe(l.getIp())),
                quote(l.getCreatedAt() == null ? "" : fmt.format(l.getCreatedAt()))
        )).collect(Collectors.joining("\n"));
        byte[] bytes = (header + body).getBytes(StandardCharsets.UTF_8);
        // 记录导出审计
        DocumentAuditLog exportLog = new DocumentAuditLog();
        exportLog.setDocumentId(docId);
        exportLog.setUserId(userId);
        exportLog.setOperation("EXPORT");
        exportLog.setCreatedAt(java.time.LocalDateTime.now());
        service.save(exportLog);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=doc-" + docId + "-audit.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(bytes);
    }

    private static String quote(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }
    private static String safe(String s) { return s == null ? "" : s; }
    private static int nullToZero(Integer i) { return i == null ? 0 : i; }
}


