package com.spice.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 虚拟磁盘空间
 *
 * @author spice
 * @date 2021/06/17 20:08
 */
@Data
public class Disk implements Serializable {

    /**
     * List<Character>: 表示一个盘块
     * List<List<Character>>: 表示所有盘块的集合，即一个磁盘
     */
    private List<List<Character>> disk;

    /**
     * 表示存储在该磁盘上的系统用户集
     */
    private Map<String, User> userMap;

    /**
     * 表示存储在该磁盘上的所有文件控制块
     */
    private List<FileControlBlock> fileControlBlockList;

    /**
     * 表示存储在该磁盘上的树形结构目录，第0个元素为根目录
     */
    private List<Directory> directoryStruct;

    /**
     * 表示存储在该磁盘上的磁盘位示图
     */
    private Integer[][] bitmap;
}
