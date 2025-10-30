package com.cowrite.project.service;

import java.time.LocalDateTime;

public interface PasswordService {
    String hashPassword(String plainPassword);
    boolean verifyPassword(String plainPassword, String hashedPassword);
    boolean isPasswordLocked(LocalDateTime lockedUntil);
}
