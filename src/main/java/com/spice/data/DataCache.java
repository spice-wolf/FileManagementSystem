package com.spice.data;

import com.spice.entity.Disk;
import lombok.Data;

/**
 * 保存程序运行所需的一些数据
 *
 * @author spice
 * @date 2021/06/17 23:58
 */
@Data
public class DataCache {

    private static volatile DataCache INSTANCE;

    /**
     * 表示一个磁盘
     */
    private Disk disk;

    public static DataCache getInstance() {
        if (INSTANCE == null) {
            synchronized (Memory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataCache();
                }
            }
        }

        return INSTANCE;
    }

    private DataCache() {
    }
}
