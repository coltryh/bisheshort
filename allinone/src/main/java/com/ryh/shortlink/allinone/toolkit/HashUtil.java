package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * 哈希工具类
 */
public class HashUtil {

    private static final String DICTIONARY = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Hash生成短链接后缀
     *
     * @param url 需要转换的URL
     * @return 短链接后缀
     */
    public static String hashToBase62(String url) {
        int i = MurmurHash.hash32(url);
        int hash = Math.abs(i % (int) Math.pow(2, 32));
        StringBuilder sb = new StringBuilder();
        while (hash > 0) {
            int index = hash % 62;
            sb.append(DICTIONARY.charAt(index));
            hash = hash / 62;
        }
        return sb.toString();
    }
}
