package com.spice.constant;

/**
 * 一些预定义好的常量
 *
 * @author spice
 * @date 2021/06/17 22:37
 */
public class DiskConstant {

    /**
     * 一个盘块的大小，单位：B
     */
    public static final Integer BLOCK_SIZE = 1024;

    /**
     * 磁盘中盘块的数量
     */
    public static final Integer BLOCK_NUM = 1024;

    /**
     * 磁盘的大小：1024 * 1024B，即1MB
     */
    public static final Integer DISK_SIZE = BLOCK_SIZE * BLOCK_NUM;

    /**
     * 磁盘中系统用户数据的起始盘块号
     */
    public static final Integer USER_START_BLOCK = 0;

    /**
     * 磁盘中系统用户数据所占用的盘块数
     */
    public static final Integer USER_BLOCK_NUM = 2;

    /**
     * 磁盘中文件控制块数据的起始盘块号
     */
    public static final Integer FCB_START_BLOCK = 2;

    /**
     * 磁盘中文件控制块数据所占用的盘块数
     */
    public static final Integer FCB_BLOCK_NUM = 2;

    /**
     * 磁盘中树形目录结构数据的起始盘块号
     */
    public static final Integer DIR_START_BLOCK = 4;

    /**
     * 磁盘中树形目录结构数据所占用的盘块数
     */
    public static final Integer DIR_BLOCK_NUM = 2;

    /**
     * 磁盘中位示图数据的起始盘块号
     */
    public static final Integer BITMAP_START_BLOCK = 6;

    /**
     * 磁盘中位示图数据所占用的盘块数
     */
    public static final Integer BITMAP_BLOCK_NUM = 1;

    /**
     * 磁盘中文件记录数据的起始盘块号
     */
    public static final Integer RECORD_START_BLOCK = 7;

    /**
     * 位示图属性：表示位示图的总行数
     */
    public static final Integer BITMAP_ROW_LENGTH = 32;

    /**
     * 位示图属性：表示位示图的总列数
     */
    public static final Integer BITMAP_LINE_LENGTH = 32;

    /**
     * 位示图属性：表示该盘块空闲
     */
    public static final Integer BITMAP_FREE = 0;

    /**
     * 位示图属性：表示该盘块被占用了
     */
    public static final Integer BITMAP_BUSY = 1;
}
