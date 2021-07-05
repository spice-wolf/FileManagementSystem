package com.spice.service.impl;

import com.spice.constant.DiskConstant;
import com.spice.entity.ActiveFile;
import com.spice.entity.FileControlBlock;
import com.spice.enums.ProtectType;
import com.spice.result.CommonResult;
import com.spice.service.DisplayService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * @author spice
 * @date 2021/06/20 2:10
 */
public class DisplayServiceImpl implements DisplayService {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    @Override
    public CommonResult<Void> printFileList(List<FileControlBlock> fileControlBlockList) {
        if (fileControlBlockList == null || fileControlBlockList.size() == 0) {
            return CommonResult.operateSuccess();
        }

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.printf("%-20s", "文件名");
        System.out.printf("%-20s", "文件类型");
        System.out.printf("%-20s", "文件长度");
        System.out.printf("%-20s", "创建时间");
        System.out.printf("%-20s", "修改时间");
        System.out.printf("%-20s", "文件属性");
        System.out.println();

        for (FileControlBlock fcb : fileControlBlockList) {
            System.out.printf("%-20s", fcb.getFileName());
            if (fcb.isDirectory()) {
                System.out.printf("%-20s", "文件夹");
                System.out.printf("%-20s", "null");
                System.out.printf("%-20s", fcb.getCreateTime().format(dtf));
                System.out.printf("%-20s", fcb.getUpdateTime().format(dtf));
                System.out.printf("%-20s", "null");
            } else {
                System.out.printf("%-20s", fcb.getSuffix() + " 文件");
                System.out.printf("%-20s", fcb.getBlockNum() + " KB");
                System.out.printf("%-20s", fcb.getCreateTime().format(dtf));
                System.out.printf("%-20s", fcb.getUpdateTime().format(dtf));
                StringBuilder protectTypeString = new StringBuilder();
                for (ProtectType protectType : fcb.getProtectTypeList()) {
                    protectTypeString.append(protectType.getDescription()).append(" | ");
                }
                System.out.printf("|%-20s", protectTypeString);
            }
            System.out.println();
        }

        System.out.println("\n----------------------------------------------------------------------------------------");
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<Void> printOpenedFile(ActiveFile activeFile) {
        if (Objects.isNull(activeFile)) {
            return CommonResult.operateFailWithMessage("[显示文件失败]: 文件为空");
        }

        System.out.println("[" + activeFile.getFileControlBlock().getFileName() + "]: ");
        StringBuilder record = new StringBuilder();
        for (Character ch : activeFile.getFileRecord()) {
            record.append(ch);
        }
        System.out.println(record);
        System.out.println("[END]");
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<Void> printHelpList() {
        System.out.println("----------------------------------------------------");
        System.out.printf("%-20s", "命令");
        System.out.printf("%-20s", "作用");
        System.out.printf("%-20s", "用法");
        System.out.println();

        System.out.printf("%-20s", "help");
        System.out.printf("%-20s", "显示帮助");
        System.out.printf("%-20s", "help");
        System.out.println();

        System.out.printf("%-20s", "show");
        System.out.printf("%-20s", "显示磁盘位示图");
        System.out.printf("%-20s", "show");
        System.out.println();

        System.out.printf("%-20s", "register");
        System.out.printf("%-20s", "用户注册");
        System.out.printf("%-20s", "register [用户名] [密码]");
        System.out.println();

        System.out.printf("%-20s", "login");
        System.out.printf("%-20s", "用户登录");
        System.out.printf("%-20s", "login [用户名] [密码]");
        System.out.println();

        System.out.printf("%-20s", "logout");
        System.out.printf("%-20s", "用户注销");
        System.out.printf("%-20s", "logout");
        System.out.println();

        System.out.printf("%-20s", "exit");
        System.out.printf("%-20s", "退出系统");
        System.out.printf("%-20s", "exit");
        System.out.println();

        System.out.printf("%-20s", "mkdir");
        System.out.printf("%-20s", "创建目录");
        System.out.printf("%-20s", "mkdir [目录名]");
        System.out.println();

        System.out.printf("%-20s", "dir");
        System.out.printf("%-20s", "列出文件");
        System.out.printf("%-20s", "dir");
        System.out.println();

        System.out.printf("%-20s", "cd");
        System.out.printf("%-20s", "切换目录");
        System.out.printf("%-20s", "cd [目录名 or 目录路径] ([cd ..]表示返回上一级目录)");
        System.out.println();

        System.out.printf("%-20s", "create");
        System.out.printf("%-20s", "创建文件");
        System.out.printf("%-20s", "create [文件名]");
        System.out.println();

        System.out.printf("%-20s", "open");
        System.out.printf("%-20s", "打开文件");
        System.out.printf("%-20s", "open [文件名 or 文件路径]");
        System.out.println();

        System.out.printf("%-20s", "read");
        System.out.printf("%-20s", "读取文件");
        System.out.printf("%-20s", "read [要读取的记录个数]");
        System.out.println();

        System.out.printf("%-20s", "write");
        System.out.printf("%-20s", "写入文件");
        System.out.printf("%-20s", "write [回车键]，然后输入要写入的内容，支持换行，以\"###\"做为写入结束标志");
        System.out.println();

        System.out.printf("%-20s", "close");
        System.out.printf("%-20s", "关闭文件");
        System.out.printf("%-20s", "close");
        System.out.println();

        System.out.printf("%-20s", "delete");
        System.out.printf("%-20s", "删除文件");
        System.out.printf("%-20s", "delete [文件名 or 文件路径]");
        System.out.println();

        System.out.printf("%-20s", "rename");
        System.out.printf("%-20s", "重命名文件");
        System.out.printf("%-20s", "rename [文件名 or 文件路径] [新文件名] (也可以重命名目录文件)");
        System.out.println();

        System.out.println("----------------------------------------------------");
        return CommonResult.operateSuccess();
    }

    @Override
    public CommonResult<Void> printBitmap(Integer[][] bitmap) {
        System.out.print("   ");
        for (int i = 0; i < DiskConstant.BITMAP_LINE_LENGTH; i++) {
            System.out.printf("%-3d", i);
        }
        System.out.println();

        System.out.print("  -");
        for (int i = 0; i < DiskConstant.BITMAP_LINE_LENGTH; i++) {
            System.out.print("---");
        }
        System.out.println();

        for (int i = 0; i < DiskConstant.BITMAP_ROW_LENGTH; i++) {
            System.out.printf("%2d", i);
            System.out.print("|[");

            int j;
            for (j = 0; j < DiskConstant.BITMAP_LINE_LENGTH - 1; j++) {
                System.out.print(bitmap[i][j] + ", ");
            }
            System.out.print(bitmap[i][j]);
            System.out.println("]");
        }
        System.out.println("Tip: 0-表示空闲，1-表示已分配");
        System.out.println("磁盘信息: 盘块数[" + DiskConstant.BLOCK_NUM + "个], 盘块大小[" + DiskConstant.BLOCK_SIZE + "B], " +
                                    "磁盘总大小[" + DiskConstant.DISK_SIZE + "B]");

        return CommonResult.operateSuccess();
    }
}
