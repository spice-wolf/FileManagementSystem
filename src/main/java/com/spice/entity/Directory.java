package com.spice.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 树形目录结构
 *
 * @author spice
 * @date 2021/06/17 21:26
 */
@Data
@Accessors(chain = true)
public class Directory implements Serializable {

    /**
     * 文件控制块
     */
    private FileControlBlock fileControlBlock;

    /**
     * 在树形目录结构中的位置
     */
    private Integer index;

    /**
     * 文件夹属性：子目录项集合
     */
    private List<Directory> childDirectory;

    /**
     * 父目录项的位置
     */
    private Integer parentIndex;
}
