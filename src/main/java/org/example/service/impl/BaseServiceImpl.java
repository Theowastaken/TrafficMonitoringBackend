package org.example.service.impl;

import org.example.common.context.CurrentUserInfo;
import org.example.util.SecurityContextUtil;
import org.example.util.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础Service类，提供通用的用户信息获取功能
 * 其他Service实现类可以继承此类来复用用户信息获取逻辑
 */
public abstract class BaseServiceImpl {

    @Autowired
    protected UserContextUtil userContextUtil;

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    BaseServiceImpl() {

    }

    /**
     * 检查当前请求是否已授权
     * @return 如果已授权返回true，否则返回false
     */
    protected boolean isAuthorized() {
        //TODO: 可以使用切面编程(AOP)来统一处理授权逻辑
        return securityContextUtil != null && securityContextUtil.getSecurityContext().isAuthenticated();
    }

    /**
     * 检查当前用户是否为管理员角色
     * @return 如果是管理员返回true，否则返回false
     */
    protected boolean isAdmin() {
        CurrentUserInfo user = getCurrentUser();
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    /**
     * 获取当前登录用户信息
     * @return 当前用户信息对象
     */
    protected CurrentUserInfo getCurrentUser() {
        return userContextUtil.getCurrentUserInfo();
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    protected String getCurrentUserId() {
        return userContextUtil.getCurrentUserId();
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    protected String getCurrentUsername() {
        return userContextUtil.getCurrentUsername();
    }
}
