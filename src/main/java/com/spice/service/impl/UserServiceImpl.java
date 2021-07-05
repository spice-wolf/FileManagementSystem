package com.spice.service.impl;

import com.spice.data.DataCache;
import com.spice.data.Memory;
import com.spice.entity.Directory;
import com.spice.entity.FileControlBlock;
import com.spice.entity.User;
import com.spice.result.CommonResult;
import com.spice.service.UserService;
import com.spice.util.StringUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author spice
 * @date 2021/06/19 1:13
 */
public class UserServiceImpl implements UserService {

    @Override
    public CommonResult<Void> register(String username, String password) {
        if (Objects.nonNull(Memory.getInstance().getCurrentUser())) {
            return CommonResult.operateFailWithMessage("[注册用户失败]: 请先注销当前已登录用户");
        }

        if (StringUtil.isAllSpace(username)) {
            return CommonResult.operateFailWithMessage("[注册用户失败]: 用户名不能为空");
        }
        if (StringUtil.isAllSpace(password)) {
            return CommonResult.operateFailWithMessage("[注册用户失败]: 密码不能为空");
        }

        User existedUser = DataCache.getInstance().getDisk().getUserMap().get(username);
        if (Objects.nonNull(existedUser)) {
            return CommonResult.operateFailWithMessage("[注册用户失败]: 该用户已存在");
        }

        // 新建一个系统用户并保存
        DataCache.getInstance().getDisk().getUserMap().put(username, new User().setUsername(username).setPassword(password));
        // 新建一个用户文件夹
        FileControlBlock fileControlBlock = new FileControlBlock()
                .setDirectory(true)
                .setFileName(username)
                .setSuffix(null)
                .setStartBlock(null)
                .setBlockNum(null)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setProtectTypeList(null);
        DataCache.getInstance().getDisk().getFileControlBlockList().add(fileControlBlock);

        // 新建一个用户目录项
        Directory directory = new Directory()
                .setFileControlBlock(fileControlBlock)
                .setChildDirectory(new LinkedList<>())
                .setParentIndex(0);
        // 保存
        DataCache.getInstance().getDisk().getDirectoryStruct().add(directory);
        directory.setIndex(DataCache.getInstance().getDisk().getDirectoryStruct().size() - 1);
        // 添加到根目录下
        DataCache.getInstance().getDisk().getDirectoryStruct().get(0).getChildDirectory().add(directory);

        return CommonResult.operateSuccessWithMessage("[注册用户成功]");
    }

    @Override
    public CommonResult<Void> login(String username, String password) {
        if (Objects.nonNull(Memory.getInstance().getCurrentUser())) {
            return CommonResult.operateFailWithMessage("[用户登录失败]: 请先注销当前已登录用户");
        }

        User user = DataCache.getInstance().getDisk().getUserMap().get(username);
        if (Objects.isNull(user)) {
            return CommonResult.operateFailWithMessage("[用户登录失败]: 该用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            return CommonResult.operateFailWithMessage("[用户登录失败]: 密码错误");
        }

        for (Directory directory : DataCache.getInstance().getDisk().getDirectoryStruct().get(0).getChildDirectory()) {
            if (directory.getFileControlBlock().isDirectory() && directory.getFileControlBlock().getFileName().equals(username)) {
                Memory.getInstance().setCurrentUser(user);
                // 切换到用户目录
                Memory.getInstance().setCurrentDirectory(directory);
                return CommonResult.operateSuccess();
            }
        }
        return CommonResult.operateFailWithMessage("[用户登录失败]: 系统错误!找不到用户目录!!");
    }

    @Override
    public CommonResult<Void> logout() {
        if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
            return CommonResult.operateFailWithMessage("[用户注销失败]: 当前没有登录用户");
        }

        Memory.getInstance().setCurrentUser(null);
        // 切换到根目录
        Memory.getInstance().setCurrentDirectory(DataCache.getInstance().getDisk().getDirectoryStruct().get(0));
        return CommonResult.operateSuccess();
    }
}
