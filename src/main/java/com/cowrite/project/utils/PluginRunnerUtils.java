package com.cowrite.project.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 插件运行工具类
 *
 * @author heathcetide
 */
public class PluginRunnerUtils {


    /**
     * 获取适合当前操作系统的Maven命令
     * @return Maven命令路径
     */
    private String getMavenCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        String mavenHome = System.getenv("MAVEN_HOME");

        if (mavenHome != null && !mavenHome.isEmpty()) {
            if (os.contains("win")) {
                // Windows系统使用mvn.cmd
                return Paths.get(mavenHome, "bin", "mvn.cmd").toString();
            } else {
                // Unix/Linux/Mac系统使用mvn
                return Paths.get(mavenHome, "bin", "mvn").toString();
            }
        } else {
            // 如果没有设置MAVEN_HOME，依赖系统PATH
            return os.contains("win") ? "mvn.cmd" : "mvn";
        }
    }

    /**
     * 编译 Spring Boot 项目并生成 JAR 文件
     *
     * @param projectDir 项目的根目录
     * @return 编译是否成功
     * @throws IOException 如果运行过程中发生 IO 错误
     */
    public boolean compileSpringBootProject(File projectDir) throws IOException {
        // 确保项目目录存在
        if (!projectDir.exists()) {
            throw new FileNotFoundException("项目目录不存在: " + projectDir.getAbsolutePath());
        }

        // 获取正确的Maven命令
        String mvnCommand = getMavenCommand();
        System.out.println(mvnCommand);
        // 构建 Maven 命令，使用compile而不是install，避免不必要的部署步骤
        List<String> command = Arrays.asList(mvnCommand, "clean", "install");

        // 使用 ProcessBuilder 来执行 Maven 构建命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(projectDir);  // 设置工作目录为项目根目录

        // 启动进程
        Process process = processBuilder.start();

        StringBuilder outputBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();

        // 读取进程的输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 打印 Maven 的构建输出
                outputBuilder.append(line).append("\n");
            }
        }

        // 读取进程的错误流
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println(errorLine);  // 打印 Maven 的错误输出
                errorBuilder.append(errorLine).append("\n");
            }
        }

        // 等待进程执行完毕
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Spring Boot 项目编译成功！");
                return true;
            } else {
                System.err.println("Spring Boot 项目编译失败，退出码：" + exitCode);
                // 分析错误原因
                analyzeCompilationErrors(outputBuilder.toString(), errorBuilder.toString());
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }


    /**
     * 分析编译错误
     * @param output 标准输出
     * @param error 错误输出
     */
    private void analyzeCompilationErrors(String output, String error) {
        if (error.contains("COMPILATION ERROR")) {
            System.err.println("检测到编译错误，请检查代码语法");
        } else if (error.contains("BUILD FAILURE")) {
            System.err.println("构建失败，请检查项目配置");
        } else if (error.contains("dependencies")) {
            System.err.println("依赖问题，请检查pom.xml文件");
        }
    }

    /**
     * 运行单元测试
     *
     * @param projectDir 项目的根目录
     * @return 测试是否通过
     * @throws IOException 如果运行过程中发生 IO 错误
     */
    public boolean runUnitTests(File projectDir) throws IOException {
        // 确保项目目录存在
        if (!projectDir.exists()) {
            throw new FileNotFoundException("项目目录不存在: " + projectDir.getAbsolutePath());
        }

        // 获取正确的Maven命令
        String mvnCommand = getMavenCommand();

        // 构建 Maven 测试命令
        List<String> command = Arrays.asList(mvnCommand, "test");

        // 使用 ProcessBuilder 来执行 Maven 测试命令
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(projectDir);  // 设置工作目录为项目根目录

        // 启动进程
        Process process = processBuilder.start();

        StringBuilder outputBuilder = new StringBuilder();
        boolean testsPassed = false;

        // 读取进程的输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 打印 Maven 的构建输出
                outputBuilder.append(line).append("\n");

                // 检测测试结果
                if ((line.contains("Tests run:") && line.contains("Failures: 0") && line.contains("Errors: 0")) || line.contains("BUILD SUCCESS")) {
                    testsPassed = true;
                }
            }
        }

        // 读取进程的错误流
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.err.println(errorLine);  // 打印 Maven 的错误输出
            }
        }

        // 等待进程执行完毕
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0 && testsPassed) {
                System.out.println("单元测试通过！");
                return true;
            } else {
                System.err.println("单元测试失败，退出码：" + exitCode);
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 运行 Spring Boot 项目的 JAR 文件
     *
     * @param jarFilePath Spring Boot 项目生成的 JAR 文件路径（相对路径）
     * @throws IOException 如果运行过程中发生 IO 错误
     */
    public void runSpringBootApplication(String jarFilePath) throws IOException {
        // 获取当前工作目录
        Path currentWorkingDirectory = Paths.get("").toAbsolutePath();

        // 将相对路径转换为绝对路径
        Path jarPath = currentWorkingDirectory.resolve(jarFilePath);

        // 检查 JAR 文件是否存在
        if (!Files.exists(jarPath)) {
            throw new FileNotFoundException("指定的 JAR 文件不存在: " + jarPath);
        }

        // 构建命令
        String command = "java -jar " + jarPath;

        // 使用 ProcessBuilder 来执行命令
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

        // 启动进程
        Process process = processBuilder.start();

        // 监听进程输出
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 监听进程错误输出
        Thread errorThread = new Thread(() -> {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println(errorLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        outputThread.start();
        errorThread.start();

        // 创建 shutdown hook 确保中断时停止进程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (process.isAlive()) {
                    System.out.println("Shutting down the Spring Boot application...");
                    process.destroy();  // 停止 Spring Boot 应用进程
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        // 等待进程执行完毕
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Spring Boot 应用运行成功！");
            } else {
                System.err.println("Spring Boot 应用运行失败，退出码：" + exitCode);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            // 结束线程
            try {
                outputThread.join();
                errorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取构建的 JAR 文件名，通常在 target 目录下
     *
     * @param projectDir 项目的根目录
     * @return 生成的 JAR 文件路径，如果不存在则返回 null
     */
    public File getJarFile(File projectDir) {
        // 获取 target 目录
        File targetDir = new File(projectDir, "target");

        // 如果 target 目录不存在或为空，返回 null
        if (!targetDir.exists() || !targetDir.isDirectory()) {
            return null;
        }

        // 过滤出所有的 JAR 文件
        FilenameFilter jarFilter = (dir, name) -> name.endsWith(".jar");
        File[] jarFiles = targetDir.listFiles(jarFilter);

        // 如果没有找到 JAR 文件，返回 null
        if (jarFiles == null || jarFiles.length == 0) {
            return null;
        }

        // 返回第一个 JAR 文件（通常只会有一个 JAR 文件）
        return jarFiles[0];
    }

    /**
     * 将本地文件转换为 MultipartFile
     */
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        String fileName = file.getName();
        String contentType = Files.probeContentType(file.toPath());
        byte[] content = Files.readAllBytes(file.toPath());

        return new MockMultipartFile(fileName, fileName, contentType, content);
    }

    /**
     * 通过 FileInputStream 转换
     */
    public static MultipartFile convertFileToMultipartFileWithStream(File file) throws IOException {
        String fileName = file.getName();
        String contentType = Files.probeContentType(file.toPath());
        FileInputStream input = new FileInputStream(file);

        return new MockMultipartFile(fileName, fileName, contentType, input);
    }


    /**
     * 编译并运行 Spring Boot 项目
     *
     * @param projectDir 项目的根目录
     * @throws IOException 如果运行过程中发生 IO 错误
     */
    public boolean compileAndTestSpringBootProject(File projectDir) throws IOException {
        // 编译项目
        boolean compileSuccess = compileSpringBootProject(projectDir);
        if (!compileSuccess) {
            System.err.println("项目编译失败，无法继续运行");
            return false;
        }

        // 运行单元测试
        boolean testSuccess = runUnitTests(projectDir);
        if (!testSuccess) {
            System.err.println("单元测试未通过，无法继续运行");
            return false;
        }
        return true;
    }

    public static boolean runCommand(File workingDir, List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }

}
