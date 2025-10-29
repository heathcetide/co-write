package com.cowrite.project.utils;

import com.cowrite.project.common.enums.PluginLanguageType;
import com.cowrite.project.exception.PluginProcessException;
import com.cowrite.project.model.entity.PluginConfig;
import com.cowrite.project.service.impl.PluginServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 插件文件工具类，提供插件文件的解压、文件验证和简单审核等功能。
 *
 * @author heathcetide
 */
public class PluginFileUtils {

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 解压 ZIP 文件到指定目录。
     *
     * @param zipFile 待解压的ZIP文件
     * @param destDir 解压后的目标目录
     * @throws IOException 如果解压过程中发生IO错误
     */
    public void unzipFile(File zipFile, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("创建目录失败: " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        throw new IOException("创建父目录失败: " + parent);
                    }

                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }


    /**
     * 用于创建文件，确保解压路径合法，防止路径穿越。
     *
     * @param destinationDir 目标解压目录
     * @param zipEntry ZIP文件条目
     * @return 解压后的文件
     * @throws IOException 如果文件路径非法
     */
    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("解压路径非法: " + zipEntry.getName());
        }
        return destFile;
    }

    /**
     * 检查插件的目录结构是否合法。此方法可以扩展为检查插件文件的必要配置文件。
     *
     * @param pluginDir 插件解压后的目录
     * @return 如果插件目录结构合法返回true，否则返回false
     */
    public boolean validatePluginStructure(File pluginDir) {
        // 假设插件必须包含一个 "plugin.json" 文件作为核心配置文件
        File descriptorFile = new File(pluginDir, "plugin.json");
        return descriptorFile.exists();
    }

    /**
     * 审核插件，检查插件是否符合预期标准。此方法可以进一步扩展以适应更多的审核规则。
     *
     * @param pluginDir 插件解压后的目录
     * @throws IllegalArgumentException 如果插件不符合审核标准
     */
    public void auditPlugin(File pluginDir) throws IllegalArgumentException {
        // 示例：检查插件是否包含插件描述文件
        if (!validatePluginStructure(pluginDir)) {
            throw new IllegalArgumentException("插件结构无效，缺少必要的插件描述文件！");
        }

        // 可以在此添加更多审核规则，如检查代码文件、扫描恶意代码等
    }

    /**
     * 获取插件目录下所有文件的路径列表。此方法可以用于进一步检查插件内容。
     *
     * @param pluginDir 插件解压后的目录
     * @return 所有文件的路径列表
     */
    public List<String> getAllFilePaths(File pluginDir) {
        List<String> filePaths = new ArrayList<>();
        try {
            Files.walk(pluginDir.toPath())
                    .filter(Files::isRegularFile)
                    .forEach(path -> filePaths.add(path.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaths;
    }

    /**
     * 检查插件是否包含可疑文件（如某些脚本文件、恶意代码等）。此方法可扩展为具体的安全检查。
     *
     * @param pluginDir 插件解压后的目录
     * @return 如果包含可疑文件返回true，否则返回false
     */
    public boolean containsSuspiciousFiles(File pluginDir) {
        List<String> filePaths = getAllFilePaths(pluginDir);

        // 示例：检查插件是否包含特定的脚本文件
        for (String filePath : filePaths) {
            if (filePath.endsWith(".sh") || filePath.endsWith(".exe")) {
                return true; // 找到可疑文件
            }
        }
        return false;
    }

    /**
     * 删除解压的插件文件。用于清理不再需要的插件文件。
     *
     * @param pluginDir 插件解压后的目录
     */
    public void deleteExtractedPluginFiles(File pluginDir) {
        try {
            Files.walk(pluginDir.toPath())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取插件的描述文件（例如 plugin.json），并进行解析。
     *
     * @param pluginDir 插件解压后的目录
     * @return 插件描述信息（JSON格式）
     * @throws IOException 如果描述文件读取失败
     */
    public String getPluginDescriptor(File pluginDir) throws IOException {
        File descriptorFile = new File(pluginDir, "plugin.json");
        if (!descriptorFile.exists()) {
            throw new IOException("插件描述文件不存在！");
        }

        // 读取文件内容并返回
        return new String(Files.readAllBytes(descriptorFile.toPath()));
    }


    /**
     * 解析插件配置文件 plugin.json 并返回插件配置对象。
     *
     * @param pluginDir 插件解压后的目录
     * @return PluginConfig 插件配置对象
     * @throws IOException 如果解析过程中发生错误
     */
    public PluginConfig parsePluginConfig(File pluginDir) throws IOException {
        File descriptorFile = new File(pluginDir, "plugin.json");

        if (!descriptorFile.exists()) {
            throw new IOException("插件描述文件不存在！");
        }

        // 解析 plugin.json 文件
        return objectMapper.readValue(descriptorFile, PluginConfig.class);
    }

    /**
     * 验证插件配置的合法性
     * @param pluginConfig 插件配置对象
     * @throws IllegalArgumentException 配置不合法时抛出异常
     */
    public void validatePluginConfig(PluginConfig pluginConfig) throws IllegalArgumentException {
        // 验证必需字段
        if (pluginConfig == null) {
            throw new IllegalArgumentException("插件配置不能为空");
        }

        if (StringUtils.isEmpty(pluginConfig.getPluginName())) {
            throw new IllegalArgumentException("插件名称不能为空");
        }

        if (StringUtils.isEmpty(pluginConfig.getVersion())) {
            throw new IllegalArgumentException("插件版本不能为空");
        }

        // 验证版本号格式 (语义化版本控制)
        if (!isValidVersionFormat(pluginConfig.getVersion())) {
            throw new IllegalArgumentException("插件版本号格式不正确，应符合语义化版本控制规范 (如: 1.0.0)");
        }

        // 验证插件名称长度
        if (pluginConfig.getPluginName().length() > 100) {
            throw new IllegalArgumentException("插件名称过长，不能超过100个字符");
        }

        // 验证描述信息
        if (pluginConfig.getDescription() != null && pluginConfig.getDescription().length() > 500) {
            throw new IllegalArgumentException("插件描述信息过长，不能超过500个字符");
        }

        // 验证作者信息
        if (StringUtils.isEmpty(pluginConfig.getAuthor())) {
            throw new IllegalArgumentException("插件作者信息不能为空");
        }
    }

    /**
     * 验证版本号格式是否符合语义化版本控制规范
     * @param version 版本号字符串
     * @return 是否符合规范
     */
    private boolean isValidVersionFormat(String version) {
        // 语义化版本控制正则表达式 (如: 1.0.0, 2.1.3-alpha.1, 1.0.0+20130313144700)
        String versionRegex = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";
        return version.matches(versionRegex);
    }

    /**
     * 对插件进行安全性检查
     * @param pluginFolder 插件目录
     * @throws SecurityException 安全检查不通过时抛出异常
     */
    public void performSecurityCheck(File pluginFolder) throws SecurityException {
        try {
            // 检查是否包含危险依赖
            checkDangerousDependencies(pluginFolder);

            // 检查是否包含恶意代码模式
            scanForMaliciousPatterns(pluginFolder);

            // 检查权限配置
            validateSecurityConfig(pluginFolder);
        } catch (Exception e) {
            throw new SecurityException("安全检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否存在危险依赖
     * @param pluginFolder 插件目录
     */
    private void checkDangerousDependencies(File pluginFolder) throws IOException {
        File pomFile = new File(pluginFolder, "pom.xml");
        if (!pomFile.exists()) {
            throw new SecurityException("缺少pom.xml文件");
        }

        String pomContent = new String(Files.readAllBytes(pomFile.toPath()));

        // 检查是否包含已知的危险依赖
        String[] dangerousArtifacts = {
                "org.apache.commons:commons-collections4:4.0", // 已知存在反序列化漏洞的版本
                // 可以继续添加其他已知危险依赖
        };

        for (String artifact : dangerousArtifacts) {
            if (pomContent.contains(artifact)) {
                throw new SecurityException("检测到危险依赖: " + artifact);
            }
        }
    }

    /**
     * 扫描恶意代码模式
     * @param pluginFolder 插件目录
     */
    private void scanForMaliciousPatterns(File pluginFolder) throws IOException {
        // 遍历所有Java文件
        scanDirectoryForPatterns(pluginFolder, ".java", Arrays.asList(
//                "Runtime.getRuntime().exec",     // 系统命令执行
                "System.exit",                   // 强制退出JVM
                "FileOutputStream.*\\.class",    // 可能的字节码操作
                "URLClassLoader"                 // 动态类加载
        ));
    }

    /**
     * 在目录中扫描特定文件类型的恶意模式
     * @param directory 目录
     * @param fileExtension 文件扩展名
     * @param patterns 恶意模式列表
     */
    private void scanDirectoryForPatterns(File directory, String fileExtension, List<String> patterns) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectoryForPatterns(file, fileExtension, patterns);
            } else if (file.getName().endsWith(fileExtension)) {
                String content = new String(Files.readAllBytes(file.toPath()));
                for (String pattern : patterns) {
                    if (content.contains(pattern)) {
                        throw new SecurityException("在文件 " + file.getPath() + " 中检测到可疑代码: " + pattern);
                    }
                }
            }
        }
    }

    /**
     * 验证安全配置
     * @param pluginFolder 插件目录
     */
    private void validateSecurityConfig(File pluginFolder) throws SecurityException {
        File applicationProperties = new File(pluginFolder, "src/main/resources/application.properties");
        if (applicationProperties.exists()) {
            // 检查是否暴露了所有端点（生产环境风险）
            try {
                String content = new String(Files.readAllBytes(applicationProperties.toPath()));
                if (content.contains("management.endpoints.web.exposure.include=*") &&
                        !content.contains("spring.profiles.active=dev")) {
                    // 仅在非开发环境警告
                    System.out.println("警告: 插件暴露了所有管理端点，可能存在安全风险");
                }
            } catch (IOException e) {
                throw new SecurityException("无法读取配置文件: " + e.getMessage());
            }
        }
    }

    public PluginLanguageType detectPluginLanguage(File pluginRootDir) {
        File[] files = pluginRootDir.listFiles();
        if (files == null) {
            return null;
        }

        for (File file : files) {
            String name = file.getName().toLowerCase();
            if (name.equals("pom.xml") || name.equals("build.gradle")) {
                return PluginLanguageType.JAVA;
            } else if (name.equals("go.mod")) {
                return PluginLanguageType.GO;
            } else if (name.equals("package.json")) {
                return PluginLanguageType.NODEJS;
            }
        }
        throw new PluginProcessException("无法识别插件类型，请确保包含 pom.xml、go.mod 或 package.json 文件");
    }
}
