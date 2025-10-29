package com.cowrite.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cowrite.project.model.entity.SocialLogin;


/**
 * SocialLogin 服务接口
 * @author Hibiscus-code-generate
 */
public interface SocialLoginService extends IService<SocialLogin> {
    SocialLogin saveSocialLogin(Long userId, String providerName, String providerUserId, String providerUsername, String accessToken, String refreshToken);
}
