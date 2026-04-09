package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.crypto.digest.DigestUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 安全工具类
 */
public class SecurityUtil {

    /**
     * MD5加密
     */
    public static String md5(String input) {
        return DigestUtil.md5Hex(input);
    }

    /**
     * SHA256加密
     */
    public static String sha256(String input) {
        return DigestUtil.sha256Hex(input);
    }

    /**
     * 生成UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的UUID
     */
    public static String generateUUID(String prefix) {
        return prefix + generateUUID();
    }

    /**
     * 密码加密（MD5 + 盐值）
     */
    public static String encryptPassword(String password, String salt) {
        return md5(md5(password) + salt);
    }

    /**
     * 验证密码
     */
    public static boolean verifyPassword(String inputPassword, String encryptedPassword, String salt) {
        return encryptPassword(inputPassword, salt).equals(encryptedPassword);
    }

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        return generateUUID().substring(0, 16);
    }

    /**
     * HTML转义（防止XSS）
     */
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    /**
     * SQL注入转义
     */
    public static String escapeSql(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "''")
                .replace("\\", "\\\\")
                .replace("--", "")
                .replace("/*", "")
                .replace("*/", "");
    }

    /**
     * URL安全 Base64 编码
     */
    public static String base64UrlEncode(byte[] bytes) {
        return java.util.Base64.getUrlEncoder().encodeToString(bytes);
    }

    /**
     * URL安全 Base64 解码
     */
    public static byte[] base64UrlDecode(String input) {
        return cn.hutool.core.codec.Base64.decode(input);
    }

    /**
     * 生成Token
     */
    public static String generateToken() {
        return generateUUID() + System.currentTimeMillis();
    }

    /**
     * 掩码手机号
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 掩码邮箱
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        if (username.length() <= 2) {
            return "**@" + parts[1];
        }
        return username.charAt(0) + "**@" + parts[1];
    }
}
