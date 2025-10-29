package com.cowrite.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.common.context.AuthContext;
import com.cowrite.project.config.GithubAuthConfig;
import com.cowrite.project.model.dto.user.UserEmailRequest;
import com.cowrite.project.model.entity.SocialLogin;
import com.cowrite.project.model.entity.User;
import com.cowrite.project.model.vo.UserVO;
import com.cowrite.project.service.SocialLoginService;
import com.cowrite.project.service.UserService;
import com.cowrite.project.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static com.cowrite.project.common.enums.ResponseCodeEnum.SYSTEM_ERROR;

/**
 * User 控制器，提供基础增删改查接口
 *
 * @author Hibiscus-code-generate
 */
@Api(tags = "User 控制器")
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * Github登录工具类
     */
    private final GithubAuthConfig githubAuthConfig;

    /**
     * 第三方登录服务
     */
    private final SocialLoginService socialLoginService;

    /**
     * 令牌工具
     */
    private final JwtUtils jwtUtils;
    public UserController(UserService userService, GithubAuthConfig githubAuthConfig, SocialLoginService socialLoginService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.githubAuthConfig = githubAuthConfig;
        this.socialLoginService = socialLoginService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 发送验证码
     */
    @GetMapping("/send-code/{email}")
    @ApiOperation("发送验证码")
    public ApiResponse<Boolean> sendVerificationCode(@PathVariable String email) throws Exception {
        Boolean isSuccess = userService.sendVerificationCode(email);
        if (isSuccess) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.error(SYSTEM_ERROR.code(), "发送失败");
        }
    }

    /**
     * 邮箱注册账号
     */
    @PostMapping("/register/email")


    @ApiOperation("邮箱注册账号")
    public ApiResponse<UserVO> registerEmail(@RequestBody UserEmailRequest userEmailRequest, HttpServletRequest request) {
        if (userEmailRequest.getCode().isEmpty() || userEmailRequest.getEmail().isEmpty()){
            return ApiResponse.error("注册信息不全");
        }
        return ApiResponse.success(userService.registerByEmail(userEmailRequest, request));
    }

    /**
     * 根据token获取信息
     */
    @GetMapping("/info")
    @ApiOperation("根据token获取角色信息")
    public ApiResponse<UserVO> getUserInfoByToken() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null){
            currentUser.setPassword(null);
            return ApiResponse.success(UserVO.convertToUserVO(currentUser));
        } else {
            return ApiResponse.error(SYSTEM_ERROR.code(), "用户不存在");
        }
    }

    /**
     * 邮箱登录
     */
    @PostMapping("/login/email")
    @ApiOperation("邮箱登录账号")
    public ApiResponse<UserVO> loginByEmail(@RequestBody UserEmailRequest userEmailRequest, HttpServletRequest request) {
        return ApiResponse.success(userService.loginByEmail(userEmailRequest, request));
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    @ApiOperation("用户退出登录")
    public ApiResponse<String> logout() {
        String currentToken = AuthContext.getCurrentToken();
        userService.logoutUser(currentToken);
        return ApiResponse.success("退出登录成功");
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    @ApiOperation("上传头像")
    public ApiResponse<String> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(file);
        return ApiResponse.success(avatarUrl);
    }

    /**
     * 普通用户修改自己的信息
     */
    @PutMapping("/update")
    @ApiOperation("普通用户修改自己的信息")
    public ApiResponse<String> updateUser(@RequestBody User user) {
        try {
            return ApiResponse.success(userService.updateUserInfo(user));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(SYSTEM_ERROR.code(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating user info", e);
            return ApiResponse.error(SYSTEM_ERROR.code(), "服务器内部错误");
        }
    }

    /**
     * 注销用户
     */
    @PostMapping("/delete-account")
    @ApiOperation("注销用户")
    public ApiResponse<Void> deleteAccount() {
        userService.requestAccountDeletion();
        return ApiResponse.success(null);
    }

    /**
     * OAuth 对接Github 登录
     */
    @GetMapping("/oauth2/code/github")
    @ApiOperation("用户Github登录")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        // 使用授权码换取 access_token
        String accessToken = githubAuthConfig.getAccessTokenFromGithub(code);

        if (accessToken != null) {
            // 使用 access_token 获取 GitHub 用户信息
            Map<String, Object> userInfo = githubAuthConfig.getUserInfoFromGithub(accessToken);
            for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
                System.out.println("-----------");
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            // 获取 GitHub 用户的唯一标识符
            Integer providerUserId = (Integer) userInfo.get("id");
            String providerUsername = (String) userInfo.get("login");
            String providerEmail = (String) userInfo.get("email");
            String avatarUrl = (String) userInfo.get("avatar_url");

            LambdaQueryWrapper<SocialLogin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SocialLogin::getProviderUserId, providerUserId);
            queryWrapper.eq(SocialLogin::getProviderName, "github");
            // 查找该 GitHub 用户是否已经在数据库中存在
            SocialLogin existingSocialLogin = socialLoginService.getOne(queryWrapper);
            if (existingSocialLogin == null) {
                // 如果不存在，先通过 UserService 创建新的用户记录
                User newUser = userService.createUserFromSocialLogin(providerUsername, providerEmail, avatarUrl);
                // 获取新创建的用户 ID
                Long userId = newUser.getId();
                // 然后创建新的社交登录记录
                SocialLogin newSocialLogin = new SocialLogin();
                newSocialLogin.setUserId(userId);
                newSocialLogin.setProviderName("github");
                newSocialLogin.setProviderUserId(String.valueOf(providerUserId));
                newSocialLogin.setProviderUsername(providerUsername);
                newSocialLogin.setAccessToken(accessToken);
                newSocialLogin.setRefreshToken(null);  // 如果没有 refresh token 可以设置为 null
                // 保存社交登录记录
                socialLoginService.save(newSocialLogin);
            }
            User user = userService.getById(existingSocialLogin.getUserId());
            // 生成 JWT token
            String jwtToken = jwtUtils.generateToken(user);
            // 构建 URL，携带 JWT 和用户信息
            String redirectUrl = "http://localhost:7070?token=" + jwtToken + "&code=" + code;
            // 重定向到前端页面，并将 token 和用户信息作为查询参数传递
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("http://localhost:7070/login-failed"); // 登录失败时跳转
        }
    }
}