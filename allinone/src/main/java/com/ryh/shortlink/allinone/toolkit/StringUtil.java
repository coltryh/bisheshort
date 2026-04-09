package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return StrUtil.isNotBlank(str);
    }

    /**
     * 字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 字符串相等的比较（忽略大小写）
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 截断字符串到指定长度
     */
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    /**
     * 截断字符串到指定长度，并添加省略号
     */
    public static String truncateWithEllipsis(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        if (maxLength <= 3) {
            return str.substring(0, maxLength);
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 左填充
     */
    public static String leftPad(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        int padLength = length - str.length();
        if (padLength <= 0) {
            return str;
        }
        StrBuilder sb = new StrBuilder();
        for (int i = 0; i < padLength; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 右填充
     */
    public static String rightPad(String str, int length, char padChar) {
        if (str == null) {
            str = "";
        }
        int padLength = length - str.length();
        if (padLength <= 0) {
            return str;
        }
        StrBuilder sb = new StrBuilder();
        sb.append(str);
        for (int i = 0; i < padLength; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderline(String camelCase) {
        if (camelCase == null || camelCase.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     */
    public static String underlineToCamel(String underline) {
        if (underline == null || underline.length() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < underline.length(); i++) {
            char c = underline.charAt(i);
            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    sb.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将逗号分隔的字符串转为列表
     */
    public static List<String> splitToList(String str, String separator) {
        if (str == null || str.length() == 0) {
            return new ArrayList<>();
        }
        String[] parts = str.split(separator);
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.length() > 0) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 将列表转为逗号分隔的字符串
     */
    public static String listToString(List<String> list, String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().collect(Collectors.joining(separator));
    }

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 重复字符串
     */
    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 替换多个字符串
     */
    public static String replaceAll(String str, String[] targets, String replacement) {
        if (str == null || targets == null || targets.length == 0) {
            return str;
        }
        String result = str;
        for (String target : targets) {
            if (target != null) {
                result = result.replace(target, replacement);
            }
        }
        return result;
    }

    /**
     * 移除前后空白
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 移除所有空白
     */
    public static String removeWhitespace(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", "");
    }
}
