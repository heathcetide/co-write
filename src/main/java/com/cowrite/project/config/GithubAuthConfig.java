package com.cowrite.project.config;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GithubAuthConfig {

    private final String clientId = "Ov23liVuTpPnLE0lVRuD";

    private final String clientSecret = "58cec9fa3ea58337cbe7934365f383f34d266385";

    private final String redirectUri = "http://localhost:8080/api/users/oauth2/code/github";


    // 使用授权码交换 access_token
    public String getAccessTokenFromGithub(String code) {
        String accessToken = null;
        String url = "https://github.com/login/oauth/access_token?" +
                "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + code +
                "&redirect_uri=" + redirectUri;

        try {
            // 使用 HTTP 请求来获取 access_token（可以使用 RestTemplate 或其他 HTTP 客户端）
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String responseBody = response.getBody();
            // 从响应中解析 access_token
            if (responseBody != null) {
                String[] params = responseBody.split("&");
                for (String param : params) {
                    if (param.startsWith("access_token=")) {
                        accessToken = param.substring("access_token=".length());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }


    // 使用 access_token 获取 GitHub 用户信息
    public Map<String, Object> getUserInfoFromGithub(String accessToken) {
        String url = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}
