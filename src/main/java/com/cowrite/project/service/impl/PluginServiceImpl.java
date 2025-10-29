package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.common.enums.PluginLanguageType;
import com.cowrite.project.common.storage.FileStorageAdapter;
import com.cowrite.project.common.storage.FileStorageProperties;
import com.cowrite.project.exception.PluginProcessException;
import com.cowrite.project.mapper.PluginMapper;
import com.cowrite.project.model.dto.plugin.PluginMongoDTO;
import com.cowrite.project.model.entity.Plugin;
import com.cowrite.project.model.entity.PluginConfig;
import com.cowrite.project.model.vo.UploadPluginVO;
import com.cowrite.project.service.PluginService;
import com.cowrite.project.utils.PluginFileUtils;
import com.cowrite.project.utils.PluginRunnerUtils;
import com.hibiscus.signal.spring.anno.SignalEmitter;
import com.hibiscus.signal.spring.configuration.SignalContextCollector;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.cowrite.project.common.constants.PluginConstants.PLUGIN_STATUS_PENDING;
import static com.cowrite.project.common.constants.PluginEventConstants.*;

/**
 * Plugin 服务实现类
 * @author Hibiscus-code-generate
 */
@Service
public class PluginServiceImpl extends ServiceImpl<PluginMapper, Plugin> implements PluginService {

    private final PluginMapper pluginMapper;

    public PluginServiceImpl(PluginMapper pluginMapper) {
        this.pluginMapper = pluginMapper;
    }
    @Resource
    private MongoTemplate mongoTemplate;
    @Override
    @SignalEmitter(CHECK_PLUGIN_EVENT)
    public UploadPluginVO savePluginFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PluginProcessException("上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null
                || !originalFilename.toLowerCase().endsWith(".zip")) {
            throw new PluginProcessException("请上传有效的 ZIP 格式插件文件");
        }

        String nameBeforeZip = originalFilename.substring(0, originalFilename.lastIndexOf(".zip"));

        Path tempDirPath;
        try {
            // 1. 创建临时目录
            tempDirPath = Files.createTempDirectory("plugin-upload-");

            // 2. 保存 ZIP
            Path zipFilePath = tempDirPath.resolve(originalFilename);
            file.transferTo(zipFilePath.toFile());

            // 3. 解压
            PluginFileUtils pluginFileUtils = new PluginFileUtils();
            pluginFileUtils.unzipFile(zipFilePath.toFile(), tempDirPath.toFile());

            // 4. 计算插件根目录
            Path pluginFolderPath = tempDirPath.resolve(nameBeforeZip);
            if (!pluginFolderPath.toFile().exists()) {
                throw new PluginProcessException("插件解压后目录不存在: " + pluginFolderPath);
            }

            // 5. 读取并校验配置
            PluginConfig pluginConfig = pluginFileUtils.parsePluginConfig(pluginFolderPath.toFile());
            System.out.println(pluginConfig);
            pluginFileUtils.validatePluginConfig(pluginConfig);
            PluginLanguageType languageType = pluginFileUtils.detectPluginLanguage(pluginFolderPath.toFile());
            SignalContextCollector.collect(PLUGIN_LANGUAGE_TYPE, languageType);
            SignalContextCollector.collect(PLUGIN_ORIGIN_FILENAME, pluginFolderPath.toAbsolutePath().toString());

            // 6. 构建插件实体对象
            Plugin build = new Plugin.Builder()
                    .pluginName(pluginConfig.getPluginName())
                    .pluginType(pluginConfig.getPluginType())
                    .description(pluginConfig.getDescription())
                    .version(pluginConfig.getVersion())
                    .webhookUrl(pluginConfig.getWebhookUrl())
                    .author(pluginConfig.getAuthor())
                    .apiUrl(pluginConfig.getApiUrls().toString())
                    .documentationUrl(pluginConfig.getDocumentationUrl())
                    .status(PLUGIN_STATUS_PENDING)
                    .enabled(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .rating(new BigDecimal(0))
                    .downloadCount(0)
                    .installCount(0)
                    .build();

            // 7. 插入数据库
            pluginMapper.insert(build);
            // 新增：同步到MongoDB
            PluginMongoDTO mongoEntity = new PluginMongoDTO();
            mongoEntity.setPluginId(build.getId()); // 插件ID
            mongoEntity.setVersion(build.getVersion()); // 版本号
            mongoEntity.setUpdateTime(build.getUpdatedAt()); // 更新时间
            mongoTemplate.save(mongoEntity);

            return UploadPluginVO.builder().id(build.getId()).status(build.getPlStatus()).build();
        } catch (Exception e) {
            log.error("插件上传处理失败: {}", e);
            throw new RuntimeException("插件上传失败，请检查格式与配置", e);
        }
    }
}
