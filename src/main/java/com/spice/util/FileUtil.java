package com.spice.util;

/**
 * 文件操作工具类
 *
 * @author spice
 * @date 2021/06/08 23:49
 */
public class FileUtil {

    /**
     * 文件名与文件后缀名之间的分隔符
     */
    private static final String SEPARATOR = ".";

    /**
     * 获取文件的后缀名
     *
     * @param fileName 文件名
     * @return 后缀名
     */
    public static String getSuffix(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return "";
        }

        int index = fileName.lastIndexOf(SEPARATOR);
        if (index == fileName.length()) {
            return "";
        }
        return fileName.substring(index + 1, fileName.length());
    }
}
