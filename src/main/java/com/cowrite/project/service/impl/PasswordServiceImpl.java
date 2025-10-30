package com.cowrite.project.service.impl;

import com.cowrite.project.service.PasswordService;
import com.cowrite.project.utils.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Override
    public String hashPassword(String plainPassword) {
        return PasswordEncoder.encode(plainPassword);
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return PasswordEncoder.matches(plainPassword, hashedPassword);
    }

    @Override
    public boolean isPasswordLocked(LocalDateTime lockedUntil) {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
}
