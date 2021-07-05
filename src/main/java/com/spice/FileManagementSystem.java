package com.spice;

import com.spice.constant.DiskConstant;
import com.spice.data.DataCache;
import com.spice.data.Memory;
import com.spice.entity.*;
import com.spice.result.CommonResult;
import com.spice.service.*;
import com.spice.service.impl.*;
import com.spice.util.StringUtil;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author spice
 * @date 2021/06/19 19:06
 */
public class FileManagementSystem {

    /**
     * 虚拟磁盘持久化文件的保存路径
     */
    private static final String SAVE_PATH = "../save/disk.ser";

    /**
     * 表示匹配多个空格
     */
    private static final String SEPARATOR = "\\s+";

    private static final String YES_RESPONSE = "yes";

    private static final UserService userService = new UserServiceImpl();
    private static final DirectoryService directoryService = new DirectoryServiceImpl();
    private static final DiskService diskService = new DiskServiceImpl();
    private static final DisplayService displayService = new DisplayServiceImpl();
    private static final FileService fileService = new FileServiceImpl();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] command;

        // 加载磁盘数据
        CommonResult<Void> loadResult = diskService.loadDisk(SAVE_PATH);
        if (loadResult.isSuccess()) {
            System.out.println(loadResult.getMessage());
        } else {
            System.out.println(loadResult.getMessage());
            System.out.print("是否初始化一个新的磁盘?(yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase(YES_RESPONSE)) {
                initDisk();
            } else {
                System.exit(0);
            }
        }

        while (true) {
            showUserAndDirectory();
            command = inputResolve(scanner.nextLine());
            switch (command[0]) {
                // 注册用户
                case "register":
                    CommonResult<Void> registerResult = userService.register(command.length > 1 ? command[1] : "",
                                                                             command.length > 2 ? command[2] : "");
                    System.out.println(registerResult.getMessage());
                    break;
                // 用户登录
                case "login":
                    CommonResult<Void> loginResult = userService.login(command.length > 1 ? command[1] : "",
                                                                       command.length > 2 ? command[2] : "");
                    if (!loginResult.isSuccess()) {
                        System.out.println(loginResult.getMessage());
                    }
                    break;
                // 用户注销
                case "logout":
                    CommonResult<Void> logoutResult = userService.logout();
                    if (!logoutResult.isSuccess()) {
                        System.out.println(logoutResult.getMessage());
                    }
                    break;
                // 创建目录
                case "mkdir":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[创建目录失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> mkdirResult = directoryService.makeDirectory(command.length > 1 ? command[1] : "");
                    if (!mkdirResult.isSuccess()) {
                        System.out.println(mkdirResult.getMessage());
                    }
                    break;
                // 切换目录
                case "cd":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[切换目录失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> cdResult = directoryService.changeDirectory(command.length > 1 ? command[1] : "");
                    if (!cdResult.isSuccess()) {
                        System.out.println(cdResult.getMessage());
                    }
                    break;
                // 查看目录
                case "dir":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[查看目录失败]: 请先登录");
                        break;
                    }
                    CommonResult<List<FileControlBlock>> dirResult = directoryService.showDirectory(Memory.getInstance().getCurrentDirectory());
                    if (dirResult.isSuccess()) {
                        displayService.printFileList(dirResult.getData());
                    } else {
                        System.out.println(dirResult.getMessage());
                    }
                    break;
                // 创建文件
                case "create":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[创建文件失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> createResult = fileService.createFile(command.length > 1 ? command[1] : "");
                    if (!createResult.isSuccess()) {
                        System.out.println(createResult.getMessage());
                    }
                    break;
                // 打开文件
                case "open":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[打开文件失败]: 请先登录");
                        break;
                    }
                    CommonResult<ActiveFile> openResult = fileService.openFile(command.length > 1 ? command[1] : "");
                    if (openResult.isSuccess()) {
                        displayService.printOpenedFile(openResult.getData());
                    } else {
                        System.out.println(openResult.getMessage());
                    }
                    break;
                // 读取文件
                case "read":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[读取文件失败]: 请先登录");
                        break;
                    }
                    try {
                        CommonResult<String> readResult = fileService.readFile(command.length > 1 ? Integer.parseInt(command[1]) : 0);
                        if (readResult.isSuccess()) {
                            System.out.println(readResult.getData());
                        } else {
                            System.out.println(readResult.getMessage());
                        }
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("[未能识别\"" + command[1] + "\"]: 请输入\"help\"查看帮助");
                        break;
                    }
                // 写入文件
                case "write":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[写入文件失败]: 请先登录");
                        break;
                    }

                    StringBuilder record = new StringBuilder();
                    // 循环读取用户的输入，直到输入的内容以"###"结尾
                    while (true) {
                        String line = scanner.nextLine();
                        if (line.endsWith("###")) {
                            record.append(line, 0, line.length() - 3);
                            break;
                        } else {
                            record.append(line).append("\n");
                        }
                    }

                    CommonResult<Void> writeResult = fileService.writeToFile(record.toString());
                    if (writeResult.isSuccess()) {
                        System.out.println();
                        // 显示修改后的文件记录
                        displayService.printOpenedFile(Memory.getInstance().getActiveFile());
                    } else {
                        System.out.println(writeResult.getMessage());
                    }
                    break;
                // 删除文件
                case "delete":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[删除文件失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> deleteResult = fileService.deleteFile(command.length > 1 ? command[1] : "");
                    if (!deleteResult.isSuccess()) {
                        System.out.println(deleteResult.getMessage());
                    }
                    break;
                // 关闭文件
                case "close":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[关闭文件失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> closeResult = fileService.closeFile();
                    if (!closeResult.isSuccess()) {
                        System.out.println(closeResult.getMessage());
                    }
                    break;
                // 重命名文件
                case "rename":
                    if (Objects.isNull(Memory.getInstance().getCurrentUser())) {
                        System.out.println("[重命名文件失败]: 请先登录");
                        break;
                    }
                    CommonResult<Void> renameResult = fileService.renameFile(command.length > 1 ? command[1] : "",
                                                                             command.length > 2 ? command[2] : "");
                    if (!renameResult.isSuccess()) {
                        System.out.println(renameResult.getMessage());
                    }
                    break;
                // 显示帮助
                case "help":
                    displayService.printHelpList();
                    break;
                // 显示位示图
                case "show":
                    displayService.printBitmap(Memory.getInstance().getBitmap());
                    break;
                // 退出系统
                case "exit":
                    diskService.saveDisk(SAVE_PATH);
                    return;
                default:
                    System.out.println("[" + command[0].toLowerCase() + "不是合法命令]: 请输入\"help\"查看帮助");
                    break;
            }
        }
    }

    /**
     * 解析用户输入的命令
     * 格式通常是：[命令] [内容]，例如：mkdir spice（创建一个名为spice的目录）
     *
     * @param input 用户的输入
     * @return 解析结果
     */
    private static String[] inputResolve(String input) {
        if (StringUtil.isAllSpace(input)) {
            return new String[]{""};
        }

        return input.trim().split(SEPARATOR);
    }

    /**
     * 显示当前用户和当前目录
     */
    private static void showUserAndDirectory() {
        StringBuilder info = new StringBuilder()
                .append("\n")
                .append("[")
                .append(Objects.nonNull(Memory.getInstance().getCurrentUser()) ?
                        Memory.getInstance().getCurrentUser().getUsername() + " " : "")
                .append(((Memory.getInstance().getCurrentDirectory().getParentIndex() == -1) ||
                        (Memory.getInstance().getCurrentDirectory().getParentIndex() == 0)) ? "/" :
                        Memory.getInstance().getCurrentDirectory().getFileControlBlock().getFileName())
                .append("] ");
        System.out.print(info);
    }

    /**
     * 初始化一个新的磁盘
     */
    private static void initDisk() {
        Disk newDisk = new Disk();

        {
            // 初始化盘块
            List<List<Character>> disk = new ArrayList<>(1024);
            newDisk.setDisk(disk);

            // 初始化位示图
            Integer[][] bitmap = new Integer[DiskConstant.BITMAP_ROW_LENGTH][DiskConstant.BITMAP_LINE_LENGTH];
            for (int i = 0; i < DiskConstant.BITMAP_ROW_LENGTH; i++) {
                for (int j = 0; j < DiskConstant.BITMAP_LINE_LENGTH; j++) {
                    bitmap[i][j] = DiskConstant.BITMAP_FREE;
                }
            }
            newDisk.setBitmap(bitmap);

            for (int i = 0; i < DiskConstant.BLOCK_NUM; i++) {
                List<Character> block = new ArrayList<>(1024);
                disk.add(block);
            }

            // 初始化系统用户数据盘块区
            for (int i = DiskConstant.USER_START_BLOCK; i < DiskConstant.USER_START_BLOCK + DiskConstant.USER_BLOCK_NUM; i++) {
                for (int j = 0; j < DiskConstant.BLOCK_SIZE; j++) {
                    newDisk.getDisk().get(i).add(j, 'U');
                    bitmap[0][i] = DiskConstant.BITMAP_BUSY;
                }
            }

            // 初始化文件控制块数据盘块区
            for (int i = DiskConstant.FCB_START_BLOCK; i < DiskConstant.FCB_START_BLOCK + DiskConstant.FCB_BLOCK_NUM; i++) {
                for (int j = 0; j < DiskConstant.BLOCK_SIZE; j++) {
                    newDisk.getDisk().get(i).add(j, 'F');
                    bitmap[0][i] = DiskConstant.BITMAP_BUSY;
                }
            }

            // 初始化树形目录结构数据盘块区
            for (int i = DiskConstant.DIR_START_BLOCK; i < DiskConstant.DIR_START_BLOCK + DiskConstant.DIR_BLOCK_NUM; i++) {
                for (int j = 0; j < DiskConstant.BLOCK_SIZE; j++) {
                    newDisk.getDisk().get(i).add(j, 'D');
                    bitmap[0][i] = DiskConstant.BITMAP_BUSY;
                }
            }

            // 初始化位示图数据盘块区
            for (int i = DiskConstant.BITMAP_START_BLOCK; i < DiskConstant.BITMAP_START_BLOCK + DiskConstant.BITMAP_BLOCK_NUM; i++) {
                for (int j = 0; j < DiskConstant.BLOCK_SIZE; j++) {
                    newDisk.getDisk().get(i).add(j, 'U');
                    bitmap[0][i] = DiskConstant.BITMAP_BUSY;
                }
            }
        }

        {
            // 初始化系统用户集
            Map<String, User> userMap = new HashMap<>(4);
            // 新增一个管理员用户
            userMap.put("Administrator", new User().setUsername("Administrator").setPassword("123456"));
            newDisk.setUserMap(userMap);
        }

        {
            // 初始化文件控制块集
            List<FileControlBlock> fileControlBlockList = new LinkedList<>();
            newDisk.setFileControlBlockList(fileControlBlockList);
            // 初始化根目录文件控制块
            FileControlBlock root = new FileControlBlock()
                    .setDirectory(true)
                    .setFileName("root")
                    .setSuffix(null)
                    .setStartBlock(null)
                    .setBlockNum(null)
                    .setProtectTypeList(null)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            fileControlBlockList.add(root);

            // 初始化Administrator目录文件控制块
            FileControlBlock administrator = new FileControlBlock()
                    .setDirectory(true)
                    .setFileName("Administrator")
                    .setSuffix(null)
                    .setStartBlock(null)
                    .setBlockNum(null)
                    .setProtectTypeList(null)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            fileControlBlockList.add(administrator);

            newDisk.setDirectoryStruct(new ArrayList<>());
            // 初始化根目录文件项
            Directory rootDirectory = new Directory()
                    .setFileControlBlock(root)
                    .setChildDirectory(new LinkedList<>())
                    .setParentIndex(-1);
            newDisk.getDirectoryStruct().add(rootDirectory);
            rootDirectory.setIndex(0);

            // 初始化Administrator目录文件项
            Directory administratorDirectory = new Directory()
                    .setFileControlBlock(administrator)
                    .setChildDirectory(new LinkedList<>())
                    .setParentIndex(0);
            newDisk.getDirectoryStruct().add(administratorDirectory);
            administratorDirectory.setIndex(newDisk.getDirectoryStruct().size() - 1);
            newDisk.getDirectoryStruct().get(0).getChildDirectory().add(administratorDirectory);
        }

        DataCache.getInstance().setDisk(newDisk);
        Memory.getInstance().setCurrentDirectory(DataCache.getInstance().getDisk().getDirectoryStruct().get(0));
        Memory.getInstance().setBitmap(DataCache.getInstance().getDisk().getBitmap());
    }
}
