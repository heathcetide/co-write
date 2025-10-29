package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SocialLogin 实体类
 * @author Hibiscus-code-generate
 */
@TableName("hib_social_logins")
public class SocialLogin extends BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId
    private Long id;

    /**
    * 关联的用户ID
    */
    @TableField("user_id")
    private Long userId;

    /**
    * 第三方登录提供商名称（如 GitHub、Google 等）
    */
    @TableField("provider_name")
    private String providerName;

    /**
    * 在第三方平台中的唯一用户标识
    */
    @TableField("provider_user_id")
    private String providerUserId;

    /**
    * 在第三方平台中的用户名
    */
    @TableField("provider_username")
    private String providerUsername;

    /**
    * 访问令牌，用于调用第三方 API
    */
    @TableField("access_token")
    private String accessToken;

    /**
    * 刷新令牌，用于刷新访问令牌
    */
    @TableField("refresh_token")
    private String refreshToken;

    public Long getId() {
    return id;
    }

    public void setId(Long id) {
    this.id = id;
    }
    public Long getUserId() {
    return userId;
    }

    public void setUserId(Long userId) {
    this.userId = userId;
    }
    public String getProviderName() {
    return providerName;
    }

    public void setProviderName(String providerName) {
    this.providerName = providerName;
    }
    public String getProviderUserId() {
    return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
    this.providerUserId = providerUserId;
    }
    public String getProviderUsername() {
    return providerUsername;
    }

    public void setProviderUsername(String providerUsername) {
    this.providerUsername = providerUsername;
    }
    public String getAccessToken() {
    return accessToken;
    }

    public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
    }
    public String getRefreshToken() {
    return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
    }

    @Override
    public SocialLogin clone() {
        try {
            return (SocialLogin) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Failed to clone User object", e);
        }
    }


    @Override
    public String toString() {
        return "SocialLogin{" +
                "id=" + id +
                ", userId=" + userId +
                ", providerName='" + providerName + '\'' +
                ", providerUserId='" + providerUserId + '\'' +
                ", providerUsername='" + providerUsername + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
