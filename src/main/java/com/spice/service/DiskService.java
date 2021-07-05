package com.spice.service;

import com.spice.entity.FileControlBlock;
import com.spice.result.CommonResult;

import java.util.List;

/**
 * 和磁盘相关的操作
 *
 * @author spice
 * @date 2021/06/17 22:40
 */
public interface DiskService {

    /**
     * 在磁盘上存储文件记录
     *
     * @param fileControlBlock 文件控制块
     * @param record 记录
     * @return 操作结果
     */
    CommonResult<FileControlBlock> storeRecord(FileControlBlock fileControlBlock, List<Character> record);

    /**
     * 释放磁盘空间（改变位示图）
     *
     * @param startBlockId 起始盘块
     * @param blockNum 盘块数
     * @return 操作结果
     */
    CommonResult<Void> freeSpace(Integer startBlockId, Integer blockNum);

    /**
     * 获取指定盘块上的文件记录
     *
     * @param startBlockId 起始盘块
     * @param blockNum 盘块数
     * @return 操作结果
     */
    CommonResult<List<Character>> getRecord(Integer startBlockId, Integer blockNum);

    /**
     * 保存虚拟磁盘数据到本地文件中
     *
     * @param savePath 保存路径
     * @return 操作结果
     */
    CommonResult<Void> saveDisk(String savePath);

    /**
     * 从一个文件中加载虚拟磁盘
     *
     * @param diskFilePath 文件
     * @return 加载结果
     */
    CommonResult<Void> loadDisk(String diskFilePath);
}
