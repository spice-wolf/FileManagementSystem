package com.spice.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 文件保护码
 *
 * @author spice
 * @date 2021/06/06 0:39
 */
public enum ProtectType {

    // 允许读
    READ(100, "可读"),
    // 允许写
    WRITE(200, "可写"),
    // 允许执行
    EXECUTE(300, "可执行");

    /**
     * 保护码
     */
    private Integer value;

    /**
     * 描述
     */
    private String description;

    ProtectType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<ProtectType> getAllPermission() {
        return Arrays.asList(values());
    }
}
