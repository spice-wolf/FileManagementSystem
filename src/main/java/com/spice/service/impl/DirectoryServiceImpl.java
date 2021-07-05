package com.spice.service.impl;

import com.spice.constant.DirectoryConstant;
import com.spice.data.DataCache;
import com.spice.data.Memory;
import com.spice.entity.Directory;
import com.spice.entity.FileControlBlock;
import com.spice.result.CommonResult;
import com.spice.service.DirectoryService;
import com.spice.util.StringUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author spice
 * @date 2021/06/17 22:19
 */
public class DirectoryServiceImpl implements DirectoryService {

    @Override
    public CommonResult<Void> makeDirectory(String directoryName) {
        if (StringUtil.isAllSpace(directoryName)) {
            return CommonResult.operateFailWithMessage("[创建目录失败]: 文件名不能为空");
        }

        // 去除前后空格
        directoryName = directoryName.trim();
        for (Directory directory : Memory.getInstance().getCurrentDirectory().getChildDirectory()) {
            if (directory.getFileControlBlock().getFileName().equalsIgnoreCase(directoryName)) {
                return CommonResult.operateFailWithMessage("[创建目录失败]: 文件名重复");
            }
        }

        // 新建目录文件的文件控制块。注意：目录文件不为其分配盘块
        FileControlBlock fileControlBlock = new FileControlBlock()
                .setDirectory(true)
                .setFileName(directoryName)
                .setSuffix(null)
                .setStartBlock(null)
                .setBlockNum(null)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setProtectTypeList(null);
        // 将文件控制块存储到磁盘中
        DataCache.getInstance().getDisk().getFileControlBlockList().add(fileControlBlock);

        // 新建一个目录项
        Directory directory = new Directory()
                .setFileControlBlock(fileControlBlock)
                .setChildDirectory(new LinkedList<>())
                .setParentIndex(DataCache.getInstance().getDisk().getDirectoryStruct().indexOf(Memory.getInstance().getCurrentDirectory()));
        // 将目录项保存到树形结构目录中
        DataCache.getInstance().getDisk().getDirectoryStruct().add(directory);
        directory.setIndex(DataCache.getInstance().getDisk().getDirectoryStruct().size() - 1);
        Memory.getInstance().getCurrentDirectory().getChildDirectory().add(directory);
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<Void> changeDirectory(String path) {
        if (StringUtil.isAllSpace(path)) {
            // 如果路径为空，直接返回当前目录
            return CommonResult.operateSuccess();
        }

        CommonResult<Directory> resolveResult = pathResolve(path);
        if (resolveResult.isSuccess()) {
            if (resolveResult.getData().getFileControlBlock().isDirectory()) {
                // 切换到目标目录
                Memory.getInstance().setCurrentDirectory(resolveResult.getData());
                return CommonResult.operateSuccess();
            } else {
                // 如果最终找到的目录项不是目录文件类型，则报错
                return CommonResult.operateFailWithMessage("[切换目录失败]: 找不到对应目录");
            }
        } else {
            return CommonResult.operateFailWithMessage("[切换目录失败]: 找不到对应目录");
        }
    }

    @Override
    public CommonResult<List<FileControlBlock>> showDirectory(Directory directory) {
        if (Objects.isNull(directory)) {
            return CommonResult.operateFailWithMessage("[显示文件失败]: 系统出错!!");
        }

        if (!directory.getFileControlBlock().isDirectory()) {
            return CommonResult.operateFailWithMessage("[显示文件失败]: " + directory.getFileControlBlock().getFileName() + "不是目录");
        }

        List<FileControlBlock> fileControlBlockList = new LinkedList<>();
        for (Directory d : directory.getChildDirectory()) {
            fileControlBlockList.add(d.getFileControlBlock());
        }
        return CommonResult.operateSuccess(fileControlBlockList);
    }

    @Override
    public CommonResult<Directory> pathResolve(String path) {
        if (StringUtil.isAllSpace(path)) {
            return CommonResult.operateFailWithMessage("[路径解析失败]: 路径不能为空");
        }

        Directory currentDirectory = Memory.getInstance().getCurrentDirectory();
        String[] pathArray = path.trim().split(DirectoryConstant.PATH_SEPARATOR);
        for (int i = 0; i < pathArray.length; i++) {
            if (isBackToPrevious(pathArray[i])) {
                if (currentDirectory.getParentIndex() != 0) {
                    // 回退到上一级目录，知道当前目录为用户的根目录
                    currentDirectory = DataCache.getInstance().getDisk().getDirectoryStruct().get(currentDirectory.getParentIndex());
                }
                continue;
            }

            if (StringUtil.isAllSpace(pathArray[i])) {
                if ((i != 0) && (i != pathArray.length - 1)) {
                    // 如果路径中间有空格则报错找不到目录（因为文件名不能为空）
                    return CommonResult.operateFailWithMessage("[路径解析失败]: 找不到对应目录");
                }
                continue;
            }

            // 寻找子目录项
            Directory childDirectory = searchChildDirectory(currentDirectory, pathArray[i]);
            if (Objects.isNull(childDirectory)) {
                return CommonResult.operateFailWithMessage("[路径解析失败]: 找不到对应文件");
            } else {
                if ((i + 1) < pathArray.length && !childDirectory.getFileControlBlock().isDirectory()) {
                    // 说明是路径的中间部分，路径的中间部分文件名应该都是对应文件夹类型才是正确的，否则报错
                    return CommonResult.operateFailWithMessage("[路径解析失败]: 找不到对应文件");
                }
                currentDirectory = childDirectory;
            }
        }

        return CommonResult.operateSuccess(currentDirectory);
    }

    /**
     * 判断是否返回上一级目录
     *
     * @param path 路径
     * @return true-返回
     */
    private boolean isBackToPrevious(String path) {
        if (path != null && path.length() >= 2) {
            // 规定: 形如".."、"../"、".. "、"  .."都是表示上一级目录的路径
            return (path.length() == 2 && DirectoryConstant.BACK_TO_PREVIOUS_ONE.equals(path)) ||
                    (path.length() > 2 && DirectoryConstant.BACK_TO_PREVIOUS_ONE.equals(path.trim()));
        }

        return false;
    }

    /**
     * 在一个目录下面寻找子目录项
     *
     * @param directory 目录文件
     * @param directoryName 子目录项文件名
     * @return 子目录项（如果找不到则为null）
     */
    private Directory searchChildDirectory(Directory directory, String directoryName) {
        if (Objects.isNull(directory)) {
            return null;
        }

        for (Directory childDirectory : directory.getChildDirectory()) {
            if (childDirectory.getFileControlBlock().getFileName().equals(directoryName)) {
                return childDirectory;
            }
        }

        return null;
    }
}
