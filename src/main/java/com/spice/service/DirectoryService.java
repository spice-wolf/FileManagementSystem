package com.spice.service;

import com.spice.entity.Directory;
import com.spice.entity.FileControlBlock;
import com.spice.result.CommonResult;

import java.util.List;

/**
 * 和目录文件相关的操作
 *
 * @author spice
 * @date 2021/06/17 22:18
 */
public interface DirectoryService {

    /**
     * 创建目录
     * 对应命令[mkdir]
     *
     * @param directoryName 目录名
     * @return 操作结果
     */
    CommonResult<Void> makeDirectory(String directoryName);

    /**
     * 改变当前目录
     * 对应命令[cd]
     *
     * @param path 路径
     * @return 操作结果
     */
    CommonResult<Void> changeDirectory(String path);

    /**
     * 列出文件目录
     * 对应命令[dir]
     *
     * @param directory 当前目录
     * @return 操作结果
     */
    CommonResult<List<FileControlBlock>> showDirectory(Directory directory);

    /**
     * 解析路径并得到目录项
     *
     * @param path 路径
     * @return 解析结果
     */
    CommonResult<Directory> pathResolve(String path);
}
