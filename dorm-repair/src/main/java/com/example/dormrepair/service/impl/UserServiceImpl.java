package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.entity.User;
import com.example.dormrepair.mapper.UserMapper;
import com.example.dormrepair.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(String username, String password, String realName, String phone, String role) {
        log.info("用户注册请求: username={}, role={}", username, role);
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            log.warn("注册失败，用户名已存在: {}", username);
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        user.setPassword(null);
        log.info("用户注册成功: userId={}, username={}", user.getId(), username);
        return user;
    }

    @Override
    public User login(String username, String password) {
        log.info("用户登录请求: username={}", username);
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("登录失败，用户不存在: {}", username);
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            log.warn("登录失败，账号被禁用: {}", username);
            throw new BusinessException("账号已被禁用");
        }
        if (!password.equals(user.getPassword())) {
            log.warn("登录失败，密码错误: {}", username);
            throw new BusinessException("用户名或密码错误");
        }
        user.setPassword(null);
        log.info("登录成功: userId={}, username={}", user.getId(), username);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码请求: userId={}", userId);
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("修改密码失败，用户不存在: userId={}", userId);
            throw new BusinessException("用户不存在");
        }
        if (!oldPassword.equals(user.getPassword())) {
            log.warn("修改密码失败，旧密码错误: userId={}", userId);
            throw new BusinessException("旧密码错误");
        }
        userMapper.updatePassword(userId, newPassword);
        log.info("修改密码成功: userId={}", userId);
    }

    @Override
    public User getById(Long id) {
        log.debug("根据ID查询用户: id={}", id);
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
            log.debug("查询到用户: username={}", user.getUsername());
        } else {
            log.warn("用户不存在: id={}", id);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        log.debug("根据用户名查询用户: username={}", username);
        return userMapper.selectByUsername(username);
    }

    // ==================== 管理员方法实现 ====================

    @Override
    public Page<User> adminPageQuery(Page<User> page, String username, String role, Integer status) {
        log.debug("管理员分页查询用户: username={}, role={}, status={}", username, role, status);
        return userMapper.selectPageFilter(page, username, role, status);
    }

    @Override
    public User adminGetById(Long id) {
        log.debug("管理员查询用户详情: id={}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("用户不存在: id={}", id);
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminCreate(User user) {
        log.info("管理员新增用户: username={}, role={}", user.getUsername(), user.getRole());
        User existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            log.warn("新增失败，用户名已存在: {}", user.getUsername());
            throw new BusinessException("用户名已存在");
        }
        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = "123456";
        }
        user.setPassword(rawPassword);
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        log.info("管理员新增用户成功: userId={}, username={}", user.getId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(User user) {
        log.info("管理员修改用户: userId={}", user.getId());
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            log.warn("修改失败，用户不存在: id={}", user.getId());
            throw new BusinessException("用户不存在");
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        log.info("管理员修改用户成功: userId={}", user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(Long id) {
        log.info("管理员删除用户: id={}", id);
        User existUser = userMapper.selectById(id);
        if (existUser == null) {
            log.warn("删除失败，用户不存在: id={}", id);
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(id);
        log.info("管理员删除用户成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer toggleStatus(Long id) {
        log.info("管理员翻转用户状态: id={}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("翻转状态失败，用户不存在: id={}", id);
            throw new BusinessException("用户不存在");
        }
        int newStatus = (user.getStatus() == null || user.getStatus() == 0) ? 1 : 0;
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setStatus(newStatus);
        userMapper.updateById(updateUser);
        log.info("用户状态翻转成功: id={}, newStatus={}", id, newStatus);
        return newStatus;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id) {
        log.info("管理员重置用户密码: id={}", id);
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("重置密码失败，用户不存在: id={}", id);
            throw new BusinessException("用户不存在");
        }
        userMapper.updatePassword(id, "123456");
        log.info("重置密码成功: id={}, 已重置为123456", id);
    }
}
