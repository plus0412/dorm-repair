package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {

    /**
     * 分页查询用户（支持按用户名、角色、状态筛选）
     */
    Page<User> selectPageFilter(Page<User> page,
                                @Param("username") String username,
                                @Param("role") String role,
                                @Param("status") Integer status);

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据 ID 查询用户
     */
    User selectById(@Param("id") Long id);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 根据 ID 更新密码（BCrypt 密文）
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 根据 ID 更新用户基本信息
     */
    int updateById(User user);

    /**
     * 根据 ID 删除用户
     */
    int deleteById(@Param("id") Long id);
}
