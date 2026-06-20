package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.User;

/**
 * 用户业务接口
 * 包含用户侧接口（注册/登录/改密）和管理员侧接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(String username, String password, String realName, String phone, String role);

    /**
     * 用户登录，返回登录后的 User 对象（不含密码）
     */
    User login(String username, String password);

    /**
     * 修改密码（需验证旧密码）
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 根据 ID 查询用户（用户侧，不含密码）
     */
    User getById(Long id);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    // ==================== 管理员方法 ====================

    /**
     * 管理员：分页查询用户（支持按用户名、角色、状态筛选）
     */
    Page<User> adminPageQuery(Page<User> page, String username, String role, Integer status);

    /**
     * 管理员：根据 ID 查询用户详情（不含密码）
     */
    User adminGetById(Long id);

    /**
     * 管理员：新增用户（密码 BCrypt 加密）
     */
    void adminCreate(User user);

    /**
     * 管理员：修改用户基本信息
     */
    void adminUpdate(User user);

    /**
     * 管理员：删除用户（物理删除）
     */
    void adminDelete(Long id);

    /**
     * 管理员：启用/禁用用户（翻转 status）
     * @return 翻转后的 status 值
     */
    Integer toggleStatus(Long id);

    /**
     * 管理员：重置密码为 123456（BCrypt 密文）
     */
    void resetPassword(Long id);
}
