package org.example.common.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前用户上下文信息
 * 用于在应用内部传递当前登录用户的身份信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserInfo {
    private String userId;
    private String username;
    private Integer role;
}
