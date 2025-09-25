package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.user.UserLoginDto;
import org.example.dto.user.UserQueryDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entity.User;
import org.example.vo.user.UserInfoVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    String login(UserLoginDto loginDto);
    
    /**
     * 用户注册
     */
    void register(UserRegisterDto registerDto);
    
    /**
     * 退出登录
     */
    void logout();
    
    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);

    /**
     * 通过用户名获取用户信息
     * @param username
     */
    public UserInfoVO getUserInfoByUserName(String username);
    
    /**
     * 分页查询用户
     */
    Page<UserInfoVO> pageUsers(UserQueryDto queryDto);
    
    /**
     * 获取用户详情
     */
    UserInfoVO getUserInfo(Long id);
    
    /**
     * 添加用户
     */
    void addUser(User user);
    
    /**
     * 更新用户
     */
    void updateUser(User user);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 重置密码
     */
    void resetPassword(Long id);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long id, Integer status);
}