package com.cowrite.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cowrite.project.mapper.SocialLoginMapper;
import com.cowrite.project.model.entity.SocialLogin;
import com.cowrite.project.service.SocialLoginService;
import org.springframework.stereotype.Service;

/**
 * SocialLogin 服务实现类
 * @author Hibiscus-code-generate
 */
@Service
public class SocialLoginServiceImpl extends ServiceImpl<SocialLoginMapper, SocialLogin>
    implements SocialLoginService {

    public SocialLogin saveSocialLogin(Long userId, String providerName, String providerUserId, String providerUsername, String accessToken, String refreshToken) {
        SocialLogin socialLogin = new SocialLogin();
        socialLogin.setUserId(userId);
        socialLogin.setProviderName(providerName);
        socialLogin.setProviderUserId(providerUserId);
        socialLogin.setProviderUsername(providerUsername);
        socialLogin.setAccessToken(accessToken);
        socialLogin.setRefreshToken(refreshToken);

        // 使用 MyBatis-Plus 的 save 方法自动保存
        this.save(socialLogin);
        return socialLogin;
    }
}
