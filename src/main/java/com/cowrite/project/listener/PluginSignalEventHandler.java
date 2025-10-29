package com.cowrite.project.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cowrite.project.common.enums.PluginLanguageType;
import com.cowrite.project.common.storage.FileStorageAdapter;
import com.cowrite.project.exception.PluginProcessException;
import com.cowrite.project.model.entity.Plugin;
import com.cowrite.project.model.vo.UploadPluginVO;
import com.cowrite.project.service.PluginService;
import com.cowrite.project.utils.PluginFileUtils;
import com.cowrite.project.utils.PluginRunnerUtils;
import com.hibiscus.signal.Signals;
import com.hibiscus.signal.core.SignalContext;
import com.hibiscus.signal.core.SignalMetrics;
import com.hibiscus.signal.spring.anno.SignalHandler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.cowrite.project.common.constants.CommonEventConstants.EVENT_INTER_MEDIATE_RESULT;
import static com.cowrite.project.common.constants.PluginEventConstants.*;
import static com.cowrite.project.utils.PluginRunnerUtils.convertFileToMultipartFile;

@Component
public class PluginSignalEventHandler {

    private final Signals signals;

    private final PluginService pluginService;

    private final FileStorageAdapter fileStorageAdapter;

    public PluginSignalEventHandler(Signals signals, PluginService pluginService, FileStorageAdapter fileStorageAdapter) {
        this.signals = signals;
        this.pluginService = pluginService;
        this.fileStorageAdapter = fileStorageAdapter;
    }
    public void printTrace(String traceId) {
        SignalMetrics metrics = signals.getMetrics();
        System.out.println(metrics);
        SignalContext context = metrics.getTrace(traceId);
        System.out.println("事件流水号: " + context.getEventId());
        System.out.println("全链路追踪ID: " + context.getTraceId());
    }
    @SignalHandler(value = CHECK_PLUGIN_EVENT, target = PluginSignalEventHandler.class, methodName = "handleCheckPlugin", timeoutMs = 1000000)
    public void handleCheckPlugin(SignalContext signalContext) throws Exception {
        PluginLanguageType pluginLanguage = PluginLanguageType.valueOf(
                String.valueOf(signalContext.getIntermediateValues().get(PLUGIN_LANGUAGE_TYPE))
        );
        Plugin byId = pluginService.getById(((UploadPluginVO) signalContext.getAttributes().get(EVENT_INTER_MEDIATE_RESULT)).getId());
        String pluginRootPath = (String) signalContext.getIntermediateValues()
                .get(PLUGIN_ORIGIN_FILENAME);


        switch (pluginLanguage) {
            case JAVA ->{
                PluginFileUtils pluginFileUtils = new PluginFileUtils();
                pluginFileUtils.performSecurityCheck(new File(pluginRootPath));
                PluginRunnerUtils pluginRunnerUtils = new PluginRunnerUtils();
                System.out.println(pluginRootPath);
                File file = new File(pluginRootPath);

                boolean success = pluginRunnerUtils.compileAndTestSpringBootProject(file);

                String upload = null;
                if (success) {
                    File jarFile = pluginRunnerUtils.getJarFile(file);
                    upload = fileStorageAdapter.upload("/plugin", convertFileToMultipartFile(jarFile));
                }
                UpdateWrapper<Plugin> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", byId.getId())
                        .set("pl_status", success ? "ACTIVE" : "REJECTED")
                        .set("updated_at", LocalDateTime.now());

                if (success && upload != null) {
                    updateWrapper.set("repository_url", upload);
                }

                pluginService.update(null, updateWrapper);
            }
            case GO -> {
                File projectDir = new File(pluginRootPath);

                // 1. 运行 go test（默认会递归测试当前模块）
                boolean testSuccess = PluginRunnerUtils.runCommand(projectDir, List.of("go", "test", "./..."));
                if (!testSuccess) {
                    UpdateWrapper<Plugin> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.eq("id", byId.getId())
                            .set("pl_status", "REJECTED")
                            .set("updated_at", LocalDateTime.now());
                    pluginService.update(null, updateWrapper);
                    throw new PluginProcessException("Go 插件测试未通过，构建终止");
                }

                // 2. 编译
                boolean buildSuccess = PluginRunnerUtils.runCommand(projectDir, List.of("go", "build", "-o", "main"));

                String upload = null;
                if (buildSuccess) {
                    File outputFile = new File(projectDir, "main");
                    if (!outputFile.exists()) {
                        throw new PluginProcessException("Go 构建成功但未找到生成文件");
                    }
                    upload = fileStorageAdapter.upload("/plugin", convertFileToMultipartFile(outputFile));
                }
                UpdateWrapper<Plugin> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", byId.getId())
                        .set("pl_status", buildSuccess ? "ACTIVE" : "REJECTED")
                        .set("updated_at", LocalDateTime.now());

                if (buildSuccess && upload != null) {
                    updateWrapper.set("repository_url", upload);
                }

                pluginService.update(null, updateWrapper);
            }
            case NODEJS -> {
                File projectDir = new File(pluginRootPath);

                boolean installSuccess = PluginRunnerUtils.runCommand(projectDir, List.of("npm", "install"));
                boolean buildSuccess = installSuccess &&
                        PluginRunnerUtils.runCommand(projectDir, List.of("npm", "run", "build"));

                String upload = null;
                if (buildSuccess) {
                    File distDir = new File(projectDir, "dist");
                    if (!distDir.exists()) {
                        throw new PluginProcessException("Node 构建成功但 dist 目录不存在");
                    }
                    Path zipPath = Files.createTempFile("plugin-dist-", ".zip");
                    try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                         ZipOutputStream zos = new ZipOutputStream(fos)) {

                        Path sourcePath = distDir.toPath();

                        Files.walk(sourcePath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                            try {
                                zos.putNextEntry(zipEntry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException("压缩目录失败: " + e.getMessage(), e);
                            }
                        });
                    }
                    File zipFile = zipPath.toFile(); // 你可能已有类似压缩工具方法
                    upload = fileStorageAdapter.upload("/plugin", convertFileToMultipartFile(zipFile));
                }
                UpdateWrapper<Plugin> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", byId.getId())
                        .set("pl_status", buildSuccess ? "ACTIVE" : "REJECTED")
                        .set("updated_at", LocalDateTime.now());

                if (buildSuccess && upload != null) {
                    updateWrapper.set("repository_url", upload);
                }

                pluginService.update(null, updateWrapper);
            }
            default -> throw new PluginProcessException("不支持的插件类型: " + pluginLanguage);
        }
        printTrace(signalContext.getTraceId());
    }
}