package com.spice.util;

import java.util.Collection;

/**
 * 集合操作工具类
 *
 * @author spice
 * @date 2021/06/08 23:32
 */
public class CollectionUtil {

    /**
     * 判断集合是否为空
     *
     * @param collection 集合
     * @return true-为空；false-不为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 集合
     * @return true-不为空；false-为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
