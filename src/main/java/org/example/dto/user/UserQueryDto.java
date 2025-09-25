package org.example.dto.user;

import lombok.Data;

/**
 * 用户查询参数DTO
 */
@Data
public class UserQueryDto {
    
    /**
     * 当前页码
     */
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 用户状态
     */
    private Integer status;
}