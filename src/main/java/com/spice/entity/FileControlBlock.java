package com.spice.entity;

import com.spice.enums.ProtectType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件控制块
 *
 * @author spice
 * @date 2021/06/17 21:01
 */
@Data
@Accessors(chain = true)
public class FileControlBlock implements Serializable {

    /**
     * 是否是目录文件
     */
    private boolean isDirectory;

    /**
     * 文件名（包括了拓展名）
     */
    private String fileName;

    /**
     * 拓展名
     */
    private String suffix;

    /**
     * 起始盘块号
     */
    private Integer startBlock;

    /**
     * 所占用的盘块数
     * 文件大小 = 一个盘块的大小 * 所占用的盘块数
     */
    private Integer blockNum;

    /**
     * 文件属性：保护码列表
     */
    private List<ProtectType> protectTypeList;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后一次修改时间
     */
    private LocalDateTime updateTime;
}
