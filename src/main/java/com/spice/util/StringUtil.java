package com.spice.util;

/**
 * 字符串操作工具类
 *
 * @author spice
 * @date 2021/06/08 23:56
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return true-为空；false-不为空
     */
    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return true-不为空；false-为空
     */
    public static boolean isNotEmpty(String str) {
       return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空，或者是否全是空格符
     *
     * @param str 字符串
     * @return true-为空，或者全是空格符
     */
    public static boolean isAllSpace(String str) {
        return (str == null || "".equals(str.trim()));
    }
}
