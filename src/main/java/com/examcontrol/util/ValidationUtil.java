package com.examcontrol.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9_]{3,50}$");

    private ValidationUtil() {}

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) return false;
        boolean hasUpper  = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower  = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit  = password.chars().anyMatch(Character::isDigit);
        return hasUpper && hasLower && hasDigit;
    }

    public static boolean isPositiveInt(String s) {
        try { return Integer.parseInt(s) > 0; }
        catch (NumberFormatException e) { return false; }
    }
}
