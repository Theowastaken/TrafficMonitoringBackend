package org.example.util;

import org.example.common.context.CurrentUserInfo;
import org.example.util.security.JwtTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 * 提供获取当前登录用户信息的通用方法
 */
@Component
public class UserContextUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public UserContextUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 从请求上下文中获取当前用户信息
     * @return 当前用户信息对象
     * @throws RuntimeException 当token无效或未授权时抛出异常
     */
    public CurrentUserInfo getCurrentUserInfo() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        String token = jwtTokenProvider.resolveToken(request);
        String userId = jwtTokenProvider.getUserId(token);
        String username = jwtTokenProvider.getUsername(token);
        Integer role = jwtTokenProvider.getRole(token);

        if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
            throw new RuntimeException("未授权或无效的token");
        }

        return new CurrentUserInfo(userId, username, role);
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public String getCurrentUserId() {
        return getCurrentUserInfo().getUserId();
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    public String getCurrentUsername() {
        return getCurrentUserInfo().getUsername();
    }
}
