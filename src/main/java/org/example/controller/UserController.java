package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.result.Result;
import org.example.dto.user.UserLoginDto;
import org.example.dto.user.UserQueryDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.vo.user.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDto loginDto) {
        String token = userService.login(loginDto);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", userService.getUserInfoByUserName(loginDto.getUsername()));
        return Result.success(result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success();
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String oldPassword = params.get("oldPassword").toString();
        String newPassword = params.get("newPassword").toString();
        userService.updatePassword(id, oldPassword, newPassword);
        return Result.success();
    }
    
    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<Page<UserInfoVO>> pageUsers(UserQueryDto queryDto) {
        Page<UserInfoVO> page = userService.pageUsers(queryDto);
        return Result.success(page);
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserInfoVO> getUserInfo(@PathVariable Long id) {
        UserInfoVO userInfo = userService.getUserInfo(id);
        return Result.success(userInfo);
    }
    
    /**
     * 添加用户
     */
    @PostMapping
    public Result<Void> addUser(@Valid @RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }
    
    /**
     * 更新用户
     */
    @PutMapping
    public Result<Void> updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        userService.updateUserStatus(id, status);
        return Result.success();
    }
}