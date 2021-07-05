package com.spice.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 系统用户
 *
 * @author spice
 * @date 2021/06/19 1:16
 */
@Data
@Accessors(chain = true)
public class User implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
