package com.spice.entity;

import lombok.Data;

import java.util.List;

/**
 * 当前打开的文件
 *
 * @author spice
 * @date 2021/06/18 2:45
 */
@Data
public class ActiveFile {

    /**
     * 文件控制块
     */
    private FileControlBlock fileControlBlock;

    /**
     * 文件记录
     */
    private List<Character> fileRecord;

    /**
     * 读指针
     */
    private Integer readPtr;

    /**
     * 写指针
     */
    private Integer writePtr;
}
