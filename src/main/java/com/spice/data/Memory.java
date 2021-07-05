package com.spice.data;

import com.spice.entity.ActiveFile;
import com.spice.entity.Directory;
import com.spice.entity.User;
import lombok.Data;

/**
 * 内存
 *
 * @author spice
 * @date 2021/06/17 22:09
 */
@Data
public class Memory {

    private static volatile Memory INSTANCE;

    /**
     * 当前登录的系统用户
     */
    private User currentUser;

    /**
     * 当前目录
     */
    private Directory currentDirectory;

    /**
     * 磁盘位示图
     */
    private Integer[][] bitmap;

    /**
     * 被调入内存的文件（当前打开的文件）
     */
    private ActiveFile activeFile;

    public static Memory getInstance() {
        if (INSTANCE == null) {
            synchronized (Memory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Memory();
                }
            }
        }

        return INSTANCE;
    }

    private Memory() {
    }
}
