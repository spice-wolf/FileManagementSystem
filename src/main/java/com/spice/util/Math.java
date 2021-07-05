package com.spice.util;

/**
 * 和计算有关的工具类
 *
 * @author spice
 * @date 2021/06/17 22:47
 */
public class Math {

    /**
     * 向上取整的除法
     *
     * @param dividend 被除数
     * @param divisor 除数
     * @return 返回 (dividend / divisor) 的向上取整结果
     */
    public static Integer ceilDivide(Integer dividend, Integer divisor) {
        if (dividend % divisor == 0) {
            // 整除
            return dividend / divisor;
        } else {
            // 不整除，向上取整
            return (dividend + divisor) / divisor;
        }
    }
}
