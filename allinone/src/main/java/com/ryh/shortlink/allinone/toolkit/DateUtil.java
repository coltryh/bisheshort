package com.ryh.shortlink.allinone.toolkit;

import cn.hutool.core.date.DateUnit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * 格式化日期为 yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    /**
     * 格式化日期为 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 格式化日期为指定格式
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 获取今天开始时间
     */
    public static Date getTodayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天结束时间
     */
    public static Date getTodayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取N天前的日期
     */
    public static Date getDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    /**
     * 获取N天后的日期
     */
    public static Date getDaysLater(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 计算两个日期之间的天数
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        return cn.hutool.core.date.DateUtil.between(startDate, endDate, DateUnit.DAY);
    }

    /**
     * 判断日期是否为今天
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        String dateStr = formatDate(date);
        String todayStr = formatDate(new Date());
        return dateStr.equals(todayStr);
    }

    /**
     * 判断日期是否在指定日期之前
     */
    public static boolean isBefore(Date date, Date target) {
        if (date == null || target == null) {
            return false;
        }
        return date.before(target);
    }

    /**
     * 判断日期是否在指定日期之后
     */
    public static boolean isAfter(Date date, Date target) {
        if (date == null || target == null) {
            return false;
        }
        return date.after(target);
    }

    /**
     * 获取日期的小时数 (0-23)
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分钟数 (0-59)
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取日期的小时分钟 (HHmm格式)
     */
    public static int getHourMinute(Date date) {
        int hour = getHour(date);
        int minute = getMinute(date);
        return hour * 100 + minute;
    }

    /**
     * 解析日期字符串
     */
    public static Date parseDate(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析 yyyy-MM-dd 格式日期
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy-MM-dd");
    }

    /**
     * 解析 yyyy-MM-dd HH:mm:ss 格式日期
     */
    public static Date parseDateTime(String dateTimeStr) {
        return parseDate(dateTimeStr, "yyyy-MM-dd HH:mm:ss");
    }
}
