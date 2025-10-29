package com.cowrite.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组织邀请码实体类， hib_org_invite。
 */
@TableName("hib_org_invite")
public class OrganizationInvite extends BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId
    private Long id;

    /**
     * 关联的组织ID
     */
    @TableField("organization_id")
    private Long organizationId;

    /**
     * 邀请者的用户ID
     */
    @TableField("inviter_id")
    private Long inviterId;

    /**
     * 邀请码，用户凭此加入组织
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 被邀请成员的角色
     */
    @TableField("role")
    private String role;

    /**
     * 邀请码最大可使用次数（null或0表示无限制）
     */
    @TableField("max_uses")
    private Integer maxUses;

    /**
     * 邀请码已使用次数
     */
    @TableField("used_count")
    private Integer usedCount;

    /**
     * 邀请码过期时间（如果为null表示永久有效）
     */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /*
    * 判断当前 OrgInvite（组织邀请）是否已过期
    * 条件1：expiresAt 不为 null（即设置了过期时间）
    * 条件2：expiresAt 的时间早于当前系统时间（LocalDateTime.now()）
    */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public OrganizationInvite clone() {
        try {
            return (OrganizationInvite) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Failed to clone User object", e);
        }
    }

    @Override
    public String toString() {
        return "OrganizationInvite{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", inviterId=" + inviterId +
                ", inviteCode='" + inviteCode + '\'' +
                ", role='" + role + '\'' +
                ", maxUses=" + maxUses +
                ", usedCount=" + usedCount +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
