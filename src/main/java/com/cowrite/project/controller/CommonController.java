package com.cowrite.project.controller;

import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.storage.FileStorageAdapter;
import com.cowrite.project.config.ServerConfig;
import com.cowrite.project.model.vo.SystemInfoVO;
import com.cowrite.project.model.vo.UploadFileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用模块
 */
@Api(tags = "Common 控制器")
@RestController
@RequestMapping("/api/common")
public class CommonController {

    private final FileStorageAdapter fileStorageAdapter;

    private final ServerConfig serverConfig;

    public CommonController(FileStorageAdapter fileStorageAdapter, ServerConfig serverConfig) {
        this.fileStorageAdapter = fileStorageAdapter;
        this.serverConfig = serverConfig;
    }


    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @ApiOperation("上传普通文件")
    public ApiResponse<UploadFileVO> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(UploadFileVO.builder()
                .filename(file.getOriginalFilename())
                .refUrl(fileStorageAdapter.upload(file)).build());
    }

    /**
     * 获取系统健康状态（内部封装调用 actuator）
     */
    @PostMapping("/system/status")
    @ApiOperation("获取系统健康状态")
    public ApiResponse<SystemInfoVO> systemStatus() {
        return ApiResponse.success(SystemInfoVO.builder()
                .healthComponent(serverConfig.getHealthComponent())
                .fontUrl(serverConfig.getFontUrl())
                .inviteBaseUrl(serverConfig.getInviteBaseUrl()).build());
    }
}
