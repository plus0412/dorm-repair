package com.example.dormrepair.service.impl;

import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.entity.User;
import com.example.dormrepair.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试
 * 使用 Mockito mock Mapper
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_shouldSuccess() {
        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register("newuser", "123456", "张三", "13800138000", "student");

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("张三", result.getRealName());
        assertNull(result.getPassword()); // 密码应被清除
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        User exist = new User();
        exist.setId(1L);

        when(userMapper.selectByUsername("admin")).thenReturn(exist);

        assertThrows(BusinessException.class, () -> {
            userService.register("admin", "123456", "管理员", "13800000000", "admin");
        });
    }

    @Test
    void login_shouldSuccess() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("admin");
        dbUser.setPassword("123456");
        dbUser.setRole("admin");
        dbUser.setStatus(1);

        when(userMapper.selectByUsername("admin")).thenReturn(dbUser);

        User result = userService.login("admin", "123456");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    void login_shouldThrowWhenUserNotExists() {
        when(userMapper.selectByUsername("notexist")).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            userService.login("notexist", "123456");
        });
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("admin");
        dbUser.setPassword("123456");
        dbUser.setStatus(1);

        when(userMapper.selectByUsername("admin")).thenReturn(dbUser);

        assertThrows(BusinessException.class, () -> {
            userService.login("admin", "wrongpass");
        });
    }

    @Test
    void login_shouldThrowWhenAccountDisabled() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("admin");
        dbUser.setPassword("123456");
        dbUser.setStatus(0); // 禁用

        when(userMapper.selectByUsername("admin")).thenReturn(dbUser);

        assertThrows(BusinessException.class, () -> {
            userService.login("admin", "123456");
        });
    }

    @Test
    void changePassword_shouldSuccess() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setPassword("123456");

        when(userMapper.selectById(1L)).thenReturn(dbUser);
        when(userMapper.updatePassword(eq(1L), anyString())).thenReturn(1);

        assertDoesNotThrow(() -> {
            userService.changePassword(1L, "123456", "newpass123");
        });
    }

    @Test
    void changePassword_shouldThrowWhenOldPasswordWrong() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setPassword("123456");

        when(userMapper.selectById(1L)).thenReturn(dbUser);

        assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, "wrongold", "newpass123");
        });
    }

    @Test
    void getById_shouldReturnUserWithoutPassword() {
        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("student");
        dbUser.setPassword("hashed");

        when(userMapper.selectById(1L)).thenReturn(dbUser);

        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals("student", result.getUsername());
        assertNull(result.getPassword());
    }
}
