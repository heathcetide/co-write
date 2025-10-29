package com.cowrite.project.utils;

import com.cowrite.project.model.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * JSON 导出工具
 *
 * @author heathcetide
 */
public class JsonExportUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 导入JSON文件
     */
    public static List<User> readUsersFromJson(MultipartFile file) throws IOException {
        return objectMapper.readValue(file.getInputStream(), new TypeReference<List<User>>() {});
    }

    /**
     * 导出用户数据为 JSON 文件
     */
    public static void exportToJson(HttpServletResponse response, List<User> users) {
        // 设置响应头
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=users.json");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 写入 JSON 数据到响应流
            objectMapper.writeValue(response.getOutputStream(), users);
        } catch (IOException e) {
            throw new RuntimeException("导出 JSON 文件失败", e);
        }
    }
}
