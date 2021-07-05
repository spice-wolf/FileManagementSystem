package com.spice.service;

import com.spice.entity.ActiveFile;
import com.spice.result.CommonResult;

/**
 * 和数据文件相关的操作
 *
 * @author spice
 * @date 2021/06/18 1:07
 */
public interface FileService {

    /**
     * 创建文件
     * 对应命令[create]
     *
     * @param fileName 文件名
     * @return 操作结果
     */
    CommonResult<Void> createFile(String fileName);

    /**
     * 打开文件
     * 对应命令[open]
     *
     * @param path 路径
     * @return 操作结果
     */
    CommonResult<ActiveFile> openFile(String path);

    /**
     * 读取文件
     * 对应命令[read]
     *
     * @param recordNum 要读取的记录数
     * @return 读取结果
     */
    CommonResult<String> readFile(Integer recordNum);

    /**
     * 写入文件
     * 对应命令[write]
     *
     * @param record 要写入的记录
     * @return 写入结果
     */
    CommonResult<Void> writeToFile(String record);

    /**
     * 关闭文件
     * 对应命令[close]
     *
     * @return 操作结果
     */
    CommonResult<Void> closeFile();

    /**
     * 删除文件（数据文件）
     * 对应命令[delete]
     *
     * @param path 文件路径
     * @return 操作结果
     */
    CommonResult<Void> deleteFile(String path);

    /**
     * 重命名文件（目录文件或者数据文件都行）
     * 对应命令[rename]
     *
     * @param path 文件路径
     * @param newName 新的文件名
     * @return 操作结果
     */
    CommonResult<Void> renameFile(String path, String newName);
}
