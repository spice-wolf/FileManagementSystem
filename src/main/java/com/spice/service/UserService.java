package com.spice.service;

import com.spice.result.CommonResult;

/**
 * 和用户相关的操作
 *
 * @author spice
 * @date 2021/06/19 1:09
 */
public interface UserService {

    /**
     * 注册一个系统用户
     * 对应命令[register]
     *
     * @param username 用户名
     * @param password 密码
     * @return 操作结果
     */
    CommonResult<Void> register(String username, String password);

    /**
     * 用户登录
     * 对应命令[login]
     *
     * @param username 用户名
     * @param password 密码
     * @return 操作结果
     */
    CommonResult<Void> login(String username, String password);

    /**
     * 用户注销
     * 对应命令[logout]
     *
     * @return 操作结果
     */
    CommonResult<Void> logout();
}
