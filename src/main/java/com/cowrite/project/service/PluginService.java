package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.entity.Plugin;
import com.cowrite.project.model.vo.UploadPluginVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Plugin 服务接口
 * @author Hibiscus-code-generate
 */
public interface PluginService extends IService<Plugin> {

    /**
     * 保存插件压缩包
     */
    UploadPluginVO savePluginFile(MultipartFile file);

}
