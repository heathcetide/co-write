package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.PageRequest;
import com.cowrite.project.common.storage.FileStorageAdapter;
import com.cowrite.project.mapper.PluginMapper;
import com.cowrite.project.model.dto.webhook.PluginUpdateNotification;
import com.cowrite.project.model.entity.Plugin;
import com.cowrite.project.model.vo.UploadPluginVO;
import com.cowrite.project.service.PluginService;
import com.cowrite.project.utils.StringUtils;
import com.cowrite.project.utils.VersionUtils;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Plugin 控制器，提供基础增删改查接口
 * @author Hibiscus-code-generate
 */
@Api(tags = "Plugin Member 控制器")
@RestController
@RequestMapping("/api/plugin")
public class PluginController {

    private final PluginService pluginService;

    private final FileStorageAdapter fileStorageAdapter;

    public PluginController(PluginService pluginService, FileStorageAdapter fileStorageAdapter) {
        this.pluginService = pluginService;
        this.fileStorageAdapter = fileStorageAdapter;
    }
    @Autowired
    private PluginMapper pluginMapper;

    // 插件本地存储目录（可在配置文件中设置）
    @Value("${plugin.local.storage-path:/data/plugins/}")
    private String localStoragePath;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 上传插件文件
     */
    @PostMapping("/upload")
    public ApiResponse<UploadPluginVO> uploadPlugin(@RequestParam("file") MultipartFile file) {
        // 在Controller层也可以添加额外的验证
        if (file.isEmpty()) {
            return ApiResponse.error("上传文件不能为空");
        }

        // 文件类型检查
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".zip")) {
            throw new IllegalArgumentException("只支持ZIP格式的插件文件");
        }
        return ApiResponse.success(pluginService.savePluginFile(file));
    }



    /**
     * 分页查询 Plugin 列表
     */
    @PostMapping("/page")
    public ApiResponse<Page<Plugin>> getPage(@RequestBody PageRequest pageRequest) {
        Page<Plugin> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        QueryWrapper<Plugin> wrapper = new QueryWrapper<>();

        if (pageRequest.getKeyword() != null && !pageRequest.getKeyword().isEmpty()) {
            wrapper.like("pluginName", pageRequest.getKeyword());
        }

        if (pageRequest.getSortBy() != null && !pageRequest.getSortBy().isEmpty()) {
            wrapper.orderBy(true, "asc".equalsIgnoreCase(pageRequest.getSortOrder()), pageRequest.getSortBy());
        }

        return ApiResponse.success(pluginService.page(page, wrapper));
    }

    @GetMapping("/download/{pluginId}")
    public ResponseEntity<StreamingResponseBody> downloadPlugin(@PathVariable("pluginId") Long pluginId) {
        Plugin plugin = pluginService.getById(pluginId);
        if (plugin == null || plugin.getRepositoryUrl() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        String url = plugin.getRepositoryUrl();

        // 提取 MinIO 对象路径
        String minioPath = extractObjectPathFromUrl(url);
        byte[] fileBytes = fileStorageAdapter.download(minioPath);

        String fileName = extractFileNameFromUrl(url); // 获取真实文件名

        StreamingResponseBody responseBody = outputStream -> {
            outputStream.write(fileBytes);
            outputStream.flush();
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileBytes.length)
                .body(responseBody);
    }

    private String extractObjectPathFromUrl(String fullUrl) {
        // 获取 endpoint，比如 http://localhost:9000
        String endpoint = "http://47.108.177.82:9000/moxie";

        if (fullUrl.startsWith(endpoint)) {
            return fullUrl.substring(endpoint.length() + 1); // 去掉 endpoint + '/'
        }

        // fallback: 提取最后两段路径作为 object path（bucket + object）
        int firstSlash = fullUrl.indexOf("://");
        if (firstSlash != -1) {
            int slashAfterHost = fullUrl.indexOf("/", firstSlash + 3);
            return fullUrl.substring(slashAfterHost + 1);
        }

        throw new RuntimeException("无法解析 MinIO 路径: " + fullUrl);
    }

    private String extractFileNameFromUrl(String fullUrl) {
        int lastSlash = fullUrl.lastIndexOf('/');
        return fullUrl.substring(lastSlash + 1);
    }

    /**
     * 设置插件的webhook回调地址（客户端调用，用于接收更新通知）
     */
    @PostMapping("/plugins/{pluginId}/webhook")
    public ApiResponse<Map> setPluginWebhook(@PathVariable Long pluginId) {

        // 1. 查询插件配置的webhookUrl
        Plugin plugin = pluginService.getById(pluginId);
        if (plugin == null) {
            return ApiResponse.error("插件不存在");
        }
        String webhookUrl = plugin.getWebhookUrl();
        if (StringUtils.isEmpty(webhookUrl)) {
            return ApiResponse.error("该插件未配置webhook地址");
        }

        // 2. 向webhookUrl发送测试请求，并获取响应
        try {
            // 构造测试请求体（模拟插件更新通知）
            Map<String, Object> testPayload = Map.of(
                    "type", "test",
                    "message", "这是一个webhook测试请求",
                    "timestamp", System.currentTimeMillis()
            );

            // 发送POST请求并接收响应
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    webhookUrl,
                    testPayload,
                    Object.class
            );
            Map result =  Map.of(
                    "webhookUrl", webhookUrl,  // 目标URL（仅显示，非响应值）
                    "statusCode", response.getStatusCodeValue(),  // 响应状态码
                    "responseBody", response.getBody()  // 响应体（核心：webhook的响应值）
            );
            // 3. 返回webhookUrl的响应内容
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("调用webhook失败：" + e.getMessage());
        }
    }

    /**
     * 客户端主动检查插件版本（作为webhook的补充机制）
     */
    @GetMapping("/plugins/{pluginId}/version")
    public ApiResponse<PluginUpdateNotification> checkPluginVersion(
            @PathVariable Long pluginId,
            @RequestParam String currentVersion) {

        Plugin latestPlugin = pluginService.getById(pluginId);
        if (latestPlugin == null) {
            return ApiResponse.error("插件不存在");
        }
        PluginUpdateNotification notification = buildUpdateNotification(latestPlugin, currentVersion);
        // 版本对比
        if (VersionUtils.isNewVersion(latestPlugin.getVersion(), currentVersion)) {

            notification.setNeedUpdate(true);
            return ApiResponse.success(notification);
        }
        notification.setNeedUpdate(false);
        return ApiResponse.success(notification); // 无更新
    }

    // 构建更新通知对象
    private PluginUpdateNotification buildUpdateNotification(Plugin plugin, String oldVersion) {
        PluginUpdateNotification notification = new PluginUpdateNotification();
        notification.setPluginId(plugin.getId());
        notification.setPluginName(plugin.getPluginName());
        notification.setOldVersion(oldVersion);
        notification.setNewVersion(plugin.getVersion());
        notification.setDownloadUrl("/api/plugins/download/" + plugin.getId()); // 下载地址
        notification.setUpdateTime(plugin.getUpdatedAt());
        return notification;
    }
}

