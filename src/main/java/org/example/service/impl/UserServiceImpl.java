package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.dto.user.UserLoginDto;
import org.example.dto.user.UserQueryDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.example.vo.user.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    private LocalStorageServiceImpl localStorageServiceImpl;

    @Override
    public String login(UserLoginDto loginDto) {
        // 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginDto.getUsername());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 验证密码
        if (!BCrypt.checkpw(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT token
        return generateToken(user.getId(), user.getUsername());
    }
    
    @Override
    public void register(UserRegisterDto registerDto) {
        // 检查用户名是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", registerDto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        wrapper = new QueryWrapper<>();
        wrapper.eq("email", registerDto.getEmail());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt()));
        user.setRealName(registerDto.getRealName());
        user.setPhone(registerDto.getPhone());
        user.setEmail(registerDto.getEmail());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
    }
    
    @Override
    public void logout() {
        // JWT无状态，退出登录由前端处理
    }
    
    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public Page<UserInfoVO> pageUsers(UserQueryDto queryDto) {
        Page<User> page = new Page<>(queryDto.getCurrent(), queryDto.getSize());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(queryDto.getUsername())) {
            wrapper.like("username", queryDto.getUsername());
        }
        if (StringUtils.hasText(queryDto.getRealName())) {
            wrapper.like("real_name", queryDto.getRealName());
        }
        if (queryDto.getStatus() != null) {
            wrapper.eq("status", queryDto.getStatus());
        }
        
        wrapper.orderByDesc("create_time");
        
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        userPage.setTotal(userMapper.selectCount(wrapper));
        // 转换为VO
        List<UserInfoVO> voList = userPage.getRecords().stream()
                .map(user -> BeanUtil.copyProperties(user, UserInfoVO.class))
                .collect(Collectors.toList());
        
        Page<UserInfoVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        voPage.setRecords(voList);
        
        return voPage;
    }

    public UserInfoVO getUserInfoByUserName(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }
    
    @Override
    public UserInfoVO getUserInfo(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }
    
    @Override
    public void addUser(User user) {
        // 检查用户名是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }
    
    @Override
    public void updateUser(User user) {
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        wrapper.ne("id", user.getId());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 更新头像URL
        user.setAvatarUrl(localStorageServiceImpl.getFileUrl(user.getAvatarBucket(), user.getAvatarObjectKey()));
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.deleteById(id);
    }
    
    @Override
    public void resetPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 重置为默认密码
        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public void updateUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    private String generateToken(Long userId, String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}