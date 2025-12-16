package com.shanzhu.cm.controller;

import com.shanzhu.cm.common.response.R;
import com.shanzhu.cm.domain.User;
import com.shanzhu.cm.domain.vo.SysUserVo;
import com.shanzhu.cm.service.SysUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户相关 控制层
 *
 * @author: ShanZhu
 * @date: 2023-12-11
 */
@RestController
public class SysUserController extends BaseController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 查询用户 带查询条件
     *
     * @param sysUser 用户信息
     * @return 用户列表
     */
    @GetMapping("/sysUser")
    public R findAllUsers(User sysUser) {
        startPage();
        List<User> data = sysUserService.findAllUsers(sysUser);
        return getResult(data);
    }

    /**
     * 通过id查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/sysUser/{id}")
    public R findUserById(@PathVariable Long id) {
        return getResult(sysUserService.findUserById(id));
    }

    /**
     * 添加用户请求，注册也在这里
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @PostMapping("/sysUser")
    public R addUser(@Validated @RequestBody User sysUser) {
        return getResult(sysUserService.addUser(sysUser));
    }

    /**
     * 更新用户
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @PutMapping("/sysUser")
    public R updateUser(@Validated @RequestBody User sysUser) {
        return getResult(sysUserService.updateUser(sysUser));
    }

    /**
     * 删除用户
     *
     * @param ids 用户id
     * @return 结果
     */
    @DeleteMapping("/sysUser/{ids}")
    public R deleteUser(@PathVariable Long[] ids) {
        return getResult(sysUserService.deleteUser(ids));
    }

    /**
     * 用户登录请求
     *
     * @param sysUserVo 登录信息
     * @return 登录结果
     */
    @RequestMapping("/sysUser/login")
    public R login(@RequestBody SysUserVo sysUserVo) {
        return getResult(sysUserService.login(sysUserVo));
    }

    /**
     * 用户注册请求
     *
     * @param sysUser 用户信息
     * @return 结果
     */
    @PostMapping("/sysUser/register")
    public R register(@Validated @RequestBody User sysUser) {
        // 注册只接收部分参数值，重新建立一个实例对象只接受注册接受的参数
        User registerUserInfo = new User();
        registerUserInfo.setUserName(sysUser.getUserName());
        registerUserInfo.setPassword(sysUser.getPassword());
        registerUserInfo.setSex(sysUser.getSex());
        registerUserInfo.setPhoneNumber(sysUser.getPhoneNumber());
        return getResult(sysUserService.addUser(registerUserInfo));
    }

}
