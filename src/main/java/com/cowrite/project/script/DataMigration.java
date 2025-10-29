package com.cowrite.project.script;

import com.cowrite.project.common.encrypt.PasswordPolicyService;
import com.cowrite.project.common.encrypt.PasswordStrengthMeter;
import com.cowrite.project.common.encrypt.PasswordStrengthMeter.PasswordStrength;
import com.cowrite.project.model.entity.User;
import com.cowrite.project.service.UserService;
import com.cowrite.project.utils.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据迁移脚本
 *
 * @author heathcetide
 */
@Component
public class DataMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigration.class);


    private final UserService userService;

    private final PasswordPolicyService passwordPolicyService;

    private final PasswordStrengthMeter passwordStrengthMeter;

    public DataMigration(UserService userService, PasswordPolicyService passwordPolicyService, PasswordStrengthMeter passwordStrengthMeter) {
        this.userService = userService;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordStrengthMeter = passwordStrengthMeter;
    }

    @Override
    public void run(String... args) {
        if (shouldMigrate()) {
            migrateUserPasswords();
        }
    }

    /**
     * 判断是否需要执行迁移逻辑：
     * 有用户，且存在未加密的 password 字段（即不为空或非空字符串）
     */
    private boolean shouldMigrate() {
        return userService.count() > 0 &&
                userService.lambdaQuery()
                        .isNotNull(User::getPassword)
                        .ne(User::getPassword, "")
                        .exists();
    }

    /**
     * 对所有用户进行密码迁移（明文 → 加密），并验证密码合规性
     */
    private void migrateUserPasswords() {
        userService.list().stream()
                .filter(user -> user.getPassword() != null && !user.getPassword().isBlank())
                .forEach(user -> {
                    String rawPassword = user.getPassword();

                    // 跳过已加密的密码
                    if (PasswordEncoder.isEncrypted(rawPassword)) {
                        log.warn("用户 {} 的密码已加密，跳过迁移", user.getUsername());
                        return;
                    }

                    // 校验密码强度
                    PasswordStrength strength = passwordStrengthMeter.measurePasswordStrength(rawPassword);
                    if (strength == PasswordStrength.WEAK) {
                        log.warn("用户 {} 的密码强度太弱，建议重置", user.getUsername());
                    }

                    // 校验密码策略
                    if (!passwordPolicyService.validatePassword(rawPassword)) {
                        log.warn("用户 {} 的密码不符合安全策略，未迁移。", user.getUsername());
                        return;
                    }

                    // 合法密码，加密并迁移
                    user.setPassword(PasswordEncoder.encode(rawPassword));
                    user.setPassword(null);
                    user.setUpdatedAt(LocalDateTime.now());

                    userService.updateById(user);
                    log.info("用户 {} 的密码迁移成功", user.getUsername());
                });
    }
}
