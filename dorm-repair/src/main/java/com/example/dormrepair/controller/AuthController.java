package com.example.dormrepair.controller;

import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.entity.User;
import com.example.dormrepair.service.UserService;
import com.example.dormrepair.common.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证接口
 * 路径前缀：/api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterRequest req) {
        log.info("接口调用: POST /api/auth/register, username={}, role={}", req.getUsername(), req.getRole());
        User user = userService.register(
                req.getUsername(),
                req.getPassword(),
                req.getRealName(),
                req.getPhone(),
                req.getRole() != null ? req.getRole() : "student"
        );

        String token = jwtUtil.generateToken(
                user.getId().intValue(),
                user.getUsername(),
                user.getRole()
        );

        LoginResult lr = new LoginResult();
        lr.setToken(token);
        lr.setUser(user);
        log.info("注册成功: userId={}, username={}", user.getId(), user.getUsername());
        return Result.success(lr);
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest req) {
        log.info("接口调用: POST /api/auth/login, username={}", req.getUsername());
        User user = userService.login(req.getUsername(), req.getPassword());

        String token = jwtUtil.generateToken(
                user.getId().intValue(),
                user.getUsername(),
                user.getRole()
        );

        LoginResult lr = new LoginResult();
        lr.setToken(token);
        lr.setUser(user);
        log.info("登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return Result.success(lr);
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/info
     * 需要 token
     */
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.debug("接口调用: GET /api/auth/info, userId={}", userId);
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * 修改密码
     * PUT /api/auth/password
     * 需要 token
     */
    @PutMapping("/password")
    public Result<?> changePassword(HttpServletRequest request,
                                  @RequestBody PasswordRequest req) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("接口调用: PUT /api/auth/password, userId={}", userId);
        userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        log.info("密码修改成功: userId={}", userId);
        return Result.success();
    }

    // 内部 DTO 类

    public static class RegisterRequest {
        private String username;
        private String password;
        private String realName;
        private String phone;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class PasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class LoginResult {
        private String token;
        private User user;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
