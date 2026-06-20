package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.User;
import com.example.dormrepair.service.UserService;
import com.example.dormrepair.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户管理（管理员）
 * 路径前缀：/api/users
 */
@RestController
@RequestMapping("/api/users")
public class UserAdminController {

    private static final Logger log = LoggerFactory.getLogger(UserAdminController.class);

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     * GET /api/users/page?pageNum=1&pageSize=10&username=xxx&role=xxx&status=x
     */
    @GetMapping("/page")
    public Result<Page<User>> page(HttpServletRequest request,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String username,
                                   @RequestParam(required = false) String role,
                                   @RequestParam(required = false) Integer status) {
        log.info("接口调用: GET /api/users/page");
        checkAdmin(request);
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> result = userService.adminPageQuery(page, username, role, status);
        return Result.success(result);
    }

    /**
     * 查询用户详情
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getById(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: GET /api/users/{}, id={}", id);
        checkAdmin(request);
        User user = userService.adminGetById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     * POST /api/users
     */
    @PostMapping
    public Result<?> create(HttpServletRequest request, @RequestBody User user) {
        log.info("接口调用: POST /api/users, username={}, role={}", user.getUsername(), user.getRole());
        checkAdmin(request);
        userService.adminCreate(user);
        return Result.success();
    }

    /**
     * 修改用户
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public Result<?> update(HttpServletRequest request, @PathVariable Long id, @RequestBody User user) {
        log.info("接口调用: PUT /api/users/{}, id={}", id);
        checkAdmin(request);
        user.setId(id);
        userService.adminUpdate(user);
        return Result.success();
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: DELETE /api/users/{}, id={}", id);
        checkAdmin(request);
        userService.adminDelete(id);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     * PUT /api/users/status/{id}
     */
    @PutMapping("/status/{id}")
    public Result<Integer> toggleStatus(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: PUT /api/users/status/{}, id={}", id);
        checkAdmin(request);
        Integer newStatus = userService.toggleStatus(id);
        return Result.success(newStatus);
    }

    /**
     * 重置密码为 123456
     * PUT /api/users/reset/{id}
     */
    @PutMapping("/reset/{id}")
    public Result<?> resetPassword(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: PUT /api/users/reset/{}, id={}", id);
        checkAdmin(request);
        userService.resetPassword(id);
        return Result.success();
    }

    /**
     * 管理员权限校验
     */
    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            throw new com.example.dormrepair.common.exception.BusinessException("无权限访问");
        }
    }
}
