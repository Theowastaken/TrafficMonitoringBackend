package org.example.common.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 安全上下文信息
 * 用于表示当前请求的认证状态
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityContext {
    private boolean isAuthenticated;
}
