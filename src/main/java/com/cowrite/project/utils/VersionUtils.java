package com.cowrite.project.utils;

public class VersionUtils {
    /**
     * 比较版本号，判断newVersion是否高于oldVersion
     */
    public static boolean isNewVersion(String newVersion, String oldVersion) {
        if (newVersion.equals(oldVersion)) {
            return false;
        }

        String[] newParts = newVersion.split("[.-]");
        String[] oldParts = oldVersion.split("[.-]");

        int maxLength = Math.max(newParts.length, oldParts.length);
        for (int i = 0; i < maxLength; i++) {
            int newPart = i < newParts.length ? parseVersionPart(newParts[i]) : 0;
            int oldPart = i < oldParts.length ? parseVersionPart(oldParts[i]) : 0;

            if (newPart > oldPart) {
                return true;
            } else if (newPart < oldPart) {
                return false;
            }
        }
        return false;
    }

    private static int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            // 非数字部分按0处理（如beta、alpha等）
            return 0;
        }
    }
}