package com.spice.service.impl;

import com.spice.constant.DiskConstant;
import com.spice.data.DataCache;
import com.spice.data.Memory;
import com.spice.entity.Disk;
import com.spice.entity.FileControlBlock;
import com.spice.result.CommonResult;
import com.spice.service.DiskService;
import com.spice.util.CollectionUtil;
import com.spice.util.Math;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author spice
 * @date 2021/06/17 22:41
 */
public class DiskServiceImpl implements DiskService {

    @Override
    public CommonResult<FileControlBlock> storeRecord(FileControlBlock fileControlBlock, List<Character> record) {
        if (CollectionUtil.isEmpty(record)) {
            return CommonResult.operateSuccess(null);
        }

        // 修改位示图，为重新分配盘块做准备
        changeBitmapStatus(fileControlBlock.getStartBlock(), fileControlBlock.getBlockNum(), true);
        // 计算存储该长度记录所需要的盘块数量
        Integer requiredNum = Math.ceilDivide(record.size(), DiskConstant.BLOCK_SIZE);

        int count = 0;
        int startBlockId = DiskConstant.RECORD_START_BLOCK;

        for (int i = 0; i < DiskConstant.BITMAP_ROW_LENGTH; i++) {
            for (int j = 0; j < DiskConstant.BITMAP_LINE_LENGTH; j++) {
                // 如果该盘块空闲
                if (DiskConstant.BITMAP_FREE.compareTo(Memory.getInstance().getBitmap()[i][j]) == 0) {
                    if (count == 0) {
                        // 记下起始盘块号: 当前行 * 总列数 + 当前列
                        startBlockId = i * DiskConstant.BITMAP_LINE_LENGTH + j;
                    }

                    count++;
                    if (count == requiredNum) {
                        // 如果有足够的连续盘区供存储，则进行存储，并改变位示图的相应状态
                        storeToDisk(startBlockId, record);
                        changeBitmapStatus(startBlockId, requiredNum, false);
                        return CommonResult.operateSuccess(fileControlBlock.setStartBlock(startBlockId).setBlockNum(requiredNum));
                    }
                } else {
                    // 因为是连续分配，如果该盘块不空闲则要重新计数
                    count = 0;
                }
            }
        }

        return CommonResult.operateFailWithMessage("[分配盘块失败]: 磁盘空间不足");
    }

    @Override
    public CommonResult<Void> freeSpace(Integer startBlockId, Integer blockNum) {
        changeBitmapStatus(startBlockId, blockNum, true);
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<List<Character>> getRecord(Integer startBlockId, Integer blockNum) {
        List<Character> record = new LinkedList<>();
        if (Objects.isNull(startBlockId)) {
            return CommonResult.operateSuccess(record);
        }

        if (startBlockId < DiskConstant.RECORD_START_BLOCK || startBlockId >= DiskConstant.BLOCK_NUM ||
            blockNum <= 0 || blockNum > DiskConstant.BLOCK_NUM) {
            return CommonResult.operateFailWithMessage("[获取记录失败]: 系统错误!!");
        }

        Disk disk = DataCache.getInstance().getDisk();
        // 盘块指针，用于从盘块中读取记录，加载进内存
        int blockPtr = 0;
        for (int i = 0; i < blockNum; ) {
            if (i == blockNum - 1) {
                if (blockPtr >= disk.getDisk().get(startBlockId + i).size() ||
                    disk.getDisk().get(startBlockId + i).get(blockPtr) == null) {
                    // 说明所有记录都读取完成了
                    break;
                }
            }

            record.add(disk.getDisk().get(startBlockId + i).get(blockPtr));
            if (blockPtr >= DiskConstant.BLOCK_SIZE) {
                blockPtr = 0;
                i++;
            } else {
                blockPtr++;
            }
        }

        return CommonResult.operateSuccess(record);
    }

    @Override
    public CommonResult<Void> saveDisk(String savePath) {
        File file = new File(savePath);
        // 检查文件是否存在
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                if (new File(file.getParentFile().getPath()).mkdirs()) {
                    try {
                        if (!file.createNewFile()) {
                            return CommonResult.operateFailWithMessage("[保存磁盘失败]: 创建保存文件失败");
                        }
                    } catch (IOException ioException) {
                        return CommonResult.operateFailWithMessage("[保存磁盘失败]: IO异常");
                    }
                } else {
                    return CommonResult.operateFailWithMessage("[保存磁盘失败]: 创建保存目录失败");
                }
            }
        }

        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            // 持久化到保存文件中
            outputStream.writeObject(DataCache.getInstance().getDisk());
            outputStream.flush();
            return CommonResult.operateSuccessWithMessage("[保存磁盘成功]");
        } catch (FileNotFoundException e) {
            return CommonResult.operateFailWithMessage("[保存磁盘失败]: 找不到文件");
        } catch (IOException ioException) {
            return CommonResult.operateFailWithMessage("[保存磁盘失败]: 保存到文件出错");
        } finally {
            try {
                if (Objects.nonNull(outputStream)) {
                    outputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public CommonResult<Void> loadDisk(String diskFilePath) {
        File file = new File(diskFilePath);
        if (!file.exists()) {
            return CommonResult.operateFailWithMessage("[加载磁盘失败]: 找不到文件");
        }

        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(file));
            // 加载磁盘数据
            DataCache.getInstance().setDisk((Disk) inputStream.readObject());
            Memory.getInstance().setCurrentDirectory(DataCache.getInstance().getDisk().getDirectoryStruct().get(0));
            Memory.getInstance().setBitmap(DataCache.getInstance().getDisk().getBitmap());
            return CommonResult.operateSuccessWithMessage("[加载磁盘成功]");
        } catch (FileNotFoundException e) {
            return CommonResult.operateFailWithMessage("[加载磁盘失败]: 找不到文件");
        } catch (IOException ioException) {
            return CommonResult.operateFailWithMessage("[加载磁盘失败]: IO异常");
        } catch (ClassNotFoundException e) {
            return CommonResult.operateFailWithMessage("[加载磁盘失败]: 非磁盘保存文件");
        } finally {
            try {
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 将记录存储到磁盘中
     *
     * @param startBlockId 起始盘块号
     * @param record 待存储的记录
     */
    private void storeToDisk(Integer startBlockId, List<Character> record) {
        int index = 0;
        int blockId = startBlockId;
        Disk disk = DataCache.getInstance().getDisk();

        // 可以直接覆盖掉原来的磁盘中的记录
        for (Character ch : record) {
            if (index >= DiskConstant.BLOCK_SIZE) {
                // 如果一个盘块的空间被用完了，则使用下一个盘块来进行存储
                blockId++;
                index = 0;
            }

            disk.getDisk().get(blockId).add(index, ch);
            index++;
        }

        while (index < DiskConstant.BLOCK_SIZE && disk.getDisk().get(blockId).size() > index) {
            // 擦除最后一个盘块中没有用到的空间
            disk.getDisk().get(blockId).set(index, null);
            index++;
        }
    }

    /**
     * 更改相应的位示图的状态
     * 仅用于连续分配
     *
     * @param startBlockId 起始盘块号
     * @param blockNum 要更改的盘块数量
     * @param changeToFree 是否改为空闲状态：true-空闲状态，false-被占用状态
     */
    private void changeBitmapStatus(Integer startBlockId, Integer blockNum, boolean changeToFree) {
        if (Objects.isNull(startBlockId) || startBlockId < DiskConstant.RECORD_START_BLOCK ||
            startBlockId >= DiskConstant.BLOCK_NUM || blockNum <= 0) {
            return;
        }

        // 解析该盘块在位示图中的第几行
        int row = startBlockId / DiskConstant.BITMAP_LINE_LENGTH;
        // 解析该盘块在位示图中的第几列
        int line = startBlockId % DiskConstant.BITMAP_LINE_LENGTH;
        for (int i = 0; i < blockNum; i++) {
            Memory.getInstance().getBitmap()[row][line] = changeToFree ? DiskConstant.BITMAP_FREE : DiskConstant.BITMAP_BUSY;
            if (line >= DiskConstant.BITMAP_LINE_LENGTH - 1) {
                line = 0;
                row++;
            } else {
                line++;
            }
        }
    }
}
