package com.spice.service;

import com.spice.entity.ActiveFile;
import com.spice.entity.FileControlBlock;
import com.spice.result.CommonResult;

import java.util.List;

/**
 * 一些显示操作
 *
 * @author spice
 * @date 2021/06/20 2:08
 */
public interface DisplayService {

    /**
     * 在控制台打印文件信息
     *
     * @param fileControlBlockList 文件控制块集合
     * @return 操作结果
     */
    CommonResult<Void> printFileList(List<FileControlBlock> fileControlBlockList);

    /**
     * 在控制台打印一个被打开的文件的信息
     *
     * @param activeFile 打开的文件
     * @return 操作结果
     */
    CommonResult<Void> printOpenedFile(ActiveFile activeFile);

    /**
     * 打印帮助列表
     * 对应命令[help]
     *
     * @return 操作结果
     */
    CommonResult<Void> printHelpList();

    /**
     * 在控制台打印磁盘位示图
     *
     * @param bitmap 位示图
     * @return 操作结果
     */
    CommonResult<Void> printBitmap(Integer[][] bitmap);
}
