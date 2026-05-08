package com.sporttracker.shared.util;

public class AuthValidator {

    private AuthValidator() {}

    public static String validateLogin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return "E-posta alanı boş bırakılamaz";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "Geçerli bir e-posta adresi giriniz";
        }
        if (email.length() > 150) {
            return "E-posta adresi çok uzun";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Şifre alanı boş bırakılamaz";
        }
        if (password.length() < 6) {
            return "Şifre en az 6 karakter olmalıdır";
        }
        return null;
    }
}
