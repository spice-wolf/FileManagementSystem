package com.spice.service.impl;

import com.spice.data.DataCache;
import com.spice.data.Memory;
import com.spice.entity.ActiveFile;
import com.spice.entity.Directory;
import com.spice.entity.FileControlBlock;
import com.spice.enums.ProtectType;
import com.spice.result.CommonResult;
import com.spice.service.DirectoryService;
import com.spice.service.DiskService;
import com.spice.service.FileService;
import com.spice.util.FileUtil;
import com.spice.util.StringUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author spice
 * @date 2021/06/18 1:07
 */
public class FileServiceImpl implements FileService {

    private final DiskService diskService = new DiskServiceImpl();

    private final DirectoryService directoryService = new DirectoryServiceImpl();

    @Override
    public CommonResult<Void> createFile(String fileName) {
        if (StringUtil.isAllSpace(fileName)) {
            return CommonResult.operateFailWithMessage("[创建文件失败]: 文件名不能为空");
        }

        // 去除前后空格
        fileName = fileName.trim();
        for (Directory directory : Memory.getInstance().getCurrentDirectory().getChildDirectory()) {
            if (directory.getFileControlBlock().getFileName().equalsIgnoreCase(fileName)) {
                return CommonResult.operateFailWithMessage("[创建文件失败]: 文件名重复");
            }
        }

        // 新建数据文件的文件控制块。注意：创建新文件的时候，文件没有记录，不为它分配盘块
        FileControlBlock fileControlBlock = new FileControlBlock()
                .setDirectory(false)
                .setFileName(fileName)
                .setSuffix(FileUtil.getSuffix(fileName))
                .setStartBlock(null)
                .setBlockNum(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setProtectTypeList(ProtectType.getAllPermission());
        // 将文件控制块存储到磁盘中
        DataCache.getInstance().getDisk().getFileControlBlockList().add(fileControlBlock);

        // 新建一个目录项
        Directory directory = new Directory()
                .setFileControlBlock(fileControlBlock)
                .setChildDirectory(null)
                .setParentIndex(DataCache.getInstance().getDisk().getDirectoryStruct().indexOf(Memory.getInstance().getCurrentDirectory()));
        // 将目录项保存到树形结构目录中
        DataCache.getInstance().getDisk().getDirectoryStruct().add(directory);
        directory.setIndex(DataCache.getInstance().getDisk().getDirectoryStruct().size() - 1);
        Memory.getInstance().getCurrentDirectory().getChildDirectory().add(directory);
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<ActiveFile> openFile(String path) {
        if (StringUtil.isAllSpace(path)) {
            return CommonResult.operateFailWithMessage("[打开文件失败]: 文件名不能为空");
        }

        if (Objects.nonNull(Memory.getInstance().getActiveFile())) {
            return CommonResult.operateFailWithMessage("[打开文件失败]: 当前存在未关闭的文件");
        }

        CommonResult<Directory> resolveResult = directoryService.pathResolve(path);
        if (resolveResult.isSuccess() && !resolveResult.getData().getFileControlBlock().isDirectory()) {
            // 将文件的记录从磁盘中读取出来
            CommonResult<List<Character>> result = diskService.getRecord(resolveResult.getData().getFileControlBlock().getStartBlock(),
                    resolveResult.getData().getFileControlBlock().getBlockNum());
            if (result.isSuccess()) {
                ActiveFile activeFile = new ActiveFile();
                activeFile.setFileControlBlock(resolveResult.getData().getFileControlBlock());
                activeFile.setFileRecord(result.getData());
                activeFile.setReadPtr(0);
                activeFile.setWritePtr(activeFile.getFileRecord().size());

                // 加载进内存中
                Memory.getInstance().setActiveFile(activeFile);
                return CommonResult.operateSuccess(activeFile);
            } else {
                return CommonResult.operateFailWithMessage("[打开文件失败]: " + result.getMessage());
            }
        }

        return CommonResult.operateFailWithMessage("[打开文件失败]: 文件不存在");
    }

    @Override
    public CommonResult<String> readFile(Integer recordNum) {
        if (Objects.isNull(recordNum) || recordNum == 0) {
            return CommonResult.operateSuccess("");
        }

        ActiveFile activeFile = Memory.getInstance().getActiveFile();
        if (Objects.isNull(activeFile)) {
            return CommonResult.operateFailWithMessage("[读取文件失败]: 请先打开文件再进行读取");
        }

        StringBuilder readResult = new StringBuilder();
        if (recordNum > 0) {
            // 向前读取记录时
            if (activeFile.getReadPtr() + recordNum > activeFile.getFileRecord().size()) {
                return CommonResult.operateFailWithMessage("[读取文件失败]: 超过文件长度");
            }

            // 读取记录
            for (int i = 0; i < recordNum; i++) {
                // 读取一个记录
                readResult.append(activeFile.getFileRecord().get(activeFile.getReadPtr()));
                // 读指针向前移动
                activeFile.setReadPtr(activeFile.getReadPtr() + 1);
            }
        } else {
            // 向后读取记录时
            if (activeFile.getReadPtr() + recordNum < 0) {
                return CommonResult.operateFailWithMessage("[读取文件失败]: 小于文件长度");
            }

            // 读取记录
            for (int i = 0; i > recordNum; i--) {
                // 读取一个记录
                readResult.insert(0, activeFile.getFileRecord().get(activeFile.getReadPtr()));
                // 读指针向后移动
                activeFile.setReadPtr(activeFile.getReadPtr() - 1);
            }
        }

        return CommonResult.operateSuccess(readResult.toString());
    }

    @Override
    public CommonResult<Void> writeToFile(String record) {
        if (StringUtil.isEmpty(record)) {
            return CommonResult.operateSuccess();
        }

        ActiveFile activeFile = Memory.getInstance().getActiveFile();
        if (Objects.isNull(activeFile)) {
            return CommonResult.operateFailWithMessage("[写入文件失败]: 请先打开文件再进行写入");
        }

        // 原文件记录
        List<Character> originalRecord = activeFile.getFileRecord();
        List<Character> toInsert = new LinkedList<>();
        for (char ch : record.toCharArray()) {
            toInsert.add(ch);
        }
        originalRecord.addAll(activeFile.getWritePtr(), toInsert);

        // 存储记录
        CommonResult<FileControlBlock> resultTwo = diskService.storeRecord(activeFile.getFileControlBlock(), originalRecord);
        if (resultTwo.isSuccess()) {
            // 设置写指针
            Memory.getInstance().getActiveFile().setWritePtr(Memory.getInstance().getActiveFile().getWritePtr() + record.length());
            // 设置内存中的文件记录
            Memory.getInstance().getActiveFile().setFileRecord(originalRecord);
            Memory.getInstance().getActiveFile().setFileControlBlock(resultTwo.getData());
            return CommonResult.operateSuccess();
        } else {
            return CommonResult.operateFailWithMessage("[写入文件失败]: " + resultTwo.getMessage());
        }
    }

    @Override
    public CommonResult<Void> closeFile() {
        if (Objects.isNull(Memory.getInstance().getActiveFile())) {
            return CommonResult.operateFailWithMessage("[关闭文件失败]: 当前没有文件被打开");
        }

        Memory.getInstance().getActiveFile().getFileControlBlock().setUpdateTime(LocalDateTime.now());
        Memory.getInstance().setActiveFile(null);
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<Void> deleteFile(String path) {
        if (StringUtil.isAllSpace(path)) {
            return CommonResult.operateFailWithMessage("[删除文件失败]: 文件名不能为空");
        }

        CommonResult<Directory> resolveResult = directoryService.pathResolve(path);
        if (resolveResult.isSuccess() && !resolveResult.getData().getFileControlBlock().isDirectory()) {
            if (Objects.nonNull(Memory.getInstance().getActiveFile()) &&
                resolveResult.getData().getFileControlBlock().equals(Memory.getInstance().getActiveFile().getFileControlBlock())) {
                return CommonResult.operateFailWithMessage("[删除文件失败]: 该文件已被打开，请先关闭");
            }

            // 如果找到目录项且是数据文件类型
            Directory parentDirectory = DataCache.getInstance().getDisk().getDirectoryStruct().get(resolveResult.getData().getParentIndex());
            parentDirectory.getChildDirectory().remove(resolveResult.getData());
            DataCache.getInstance().getDisk().getDirectoryStruct().set(resolveResult.getData().getIndex(), null);
            // 释放空间
            diskService.freeSpace(resolveResult.getData().getFileControlBlock().getStartBlock(),
                    resolveResult.getData().getFileControlBlock().getBlockNum());
            return CommonResult.operateSuccess();
        } else {
            return CommonResult.operateFailWithMessage("[删除文件失败]: 找不到对应文件");
        }
    }

    @Override
    public CommonResult<Void> renameFile(String path, String newName) {
        if (StringUtil.isAllSpace(path) || StringUtil.isAllSpace(newName)) {
            return CommonResult.operateFailWithMessage("[重命名文件失败]: 文件名不能为空");
        }

        CommonResult<Directory> resolveResult = directoryService.pathResolve(path);
        if (resolveResult.isSuccess()) {
            Directory parentDirectory = DataCache.getInstance().getDisk().getDirectoryStruct().get(resolveResult.getData().getParentIndex());
            for (Directory childDirectory : parentDirectory.getChildDirectory()) {
                if (childDirectory.getFileControlBlock().getFileName().equalsIgnoreCase(newName)) {
                    return CommonResult.operateFailWithMessage("[重命名文件失败]: 文件名重复");
                }
            }

            resolveResult.getData().getFileControlBlock()
                    .setFileName(newName)
                    .setSuffix(resolveResult.getData().getFileControlBlock().isDirectory() ? null : FileUtil.getSuffix(newName))
                    .setUpdateTime(LocalDateTime.now());
            return CommonResult.operateSuccess();
        }
        return CommonResult.operateFailWithMessage("[重命名文件失败]: 文件不存在");
    }
}
