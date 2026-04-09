package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtil {

    /**
     * 邮箱正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 手机号正则（中国大陆）
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * URL正则
     */
    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://[a-zA-Z0-9\\-.]+(:\\d+)?(/.*)?$");

    /**
     * 验证邮箱
     */
    public static boolean isEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 验证手机号
     */
    public static boolean isPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证URL
     */
    public static boolean isUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 验证用户名（字母、数字、下划线，4-20位）
     */
    public static boolean isValidUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]{4,20}$");
    }

    /**
     * 验证密码（至少8位，包含字母和数字）
     */
    public static boolean isValidPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        // 包含字母
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        // 包含数字
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }

    /**
     * 验证IP地址
     */
    public static boolean isIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return false;
        }
        return Validator.isIpv4(ip) || Validator.isIpv6(ip);
    }

    /**
     * 验证是否为数字
     */
    public static boolean isNumeric(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * 验证是否为整数
     */
    public static boolean isInteger(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches("-?\\d+");
    }

    /**
     * 验证是否为正整数
     */
    public static boolean isPositiveInteger(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * 验证是否为小数
     */
    public static boolean isDecimal(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return str.matches("-?\\d+\\.\\d+");
    }

    /**
     * 验证长度范围
     */
    public static boolean isLengthInRange(String str, int min, int max) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    /**
     * 验证不为空
     */
    public static boolean isNotEmpty(String str) {
        return StrUtil.isNotBlank(str);
    }

    /**
     * 验证为空
     */
    public static boolean isEmpty(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 验证短链接格式
     */
    public static boolean isValidShortUri(String shortUri) {
        if (StrUtil.isBlank(shortUri)) {
            return false;
        }
        // 短链接格式：6-10位字母数字组合
        return shortUri.matches("^[a-zA-Z0-9]{6,10}$");
    }

    /**
     * 验证分组ID格式
     */
    public static boolean isValidGid(String gid) {
        if (StrUtil.isBlank(gid)) {
            return false;
        }
        // 分组ID格式：6位字母数字组合
        return gid.matches("^[a-zA-Z0-9]{6}$");
    }

    /**
     * 验证描述长度（最大500字符）
     */
    public static boolean isValidDescribe(String describe) {
        if (describe == null) {
            return true;
        }
        return describe.length() <= 500;
    }
}
