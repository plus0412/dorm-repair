package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairType;
import com.example.dormrepair.service.RepairTypeService;
import com.example.dormrepair.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 报修类型管理（管理员）
 * 路径前缀：/api/repair-types
 */
@RestController
@RequestMapping("/api/repair-types")
public class RepairTypeController {

    private static final Logger log = LoggerFactory.getLogger(RepairTypeController.class);

    @Autowired
    private RepairTypeService repairTypeService;

    /**
     * 分页查询
     * GET /api/repair-types/page?pageNum=1&pageSize=10&name=xxx&status=
     */
    @GetMapping("/page")
    public Result<Page<RepairType>> page(HttpServletRequest request,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer status) {
        log.info("接口调用: GET /api/repair-types/page");
        checkAdmin(request);
        Page<RepairType> page = new Page<>(pageNum, pageSize);
        Page<RepairType> result = repairTypeService.selectPageFilter(page, name, status);
        return Result.success(result);
    }

    /**
     * 列表查询（下拉用，仅启用状态）
     * GET /api/repair-types/list
     * 所有登录用户都可访问（用于报修表单选择）
     */
    @GetMapping("/list")
    public Result<List<RepairType>> list(HttpServletRequest request) {
        log.info("接口调用: GET /api/repair-types/list");
        // 不需要管理员权限，所有登录用户都可访问
        List<RepairType> list = repairTypeService.selectListEnabled();
        return Result.success(list);
    }

    /**
     * 根据 ID 查询
     * GET /api/repair-types/{id}
     */
    @GetMapping("/{id}")
    public Result<RepairType> getById(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: GET /api/repair-types/{}, id={}", id);
        checkAdmin(request);
        RepairType repairType = repairTypeService.getById(id);
        return Result.success(repairType);
    }

    /**
     * 新增
     * POST /api/repair-types
     */
    @PostMapping
    public Result<?> create(HttpServletRequest request, @RequestBody RepairType repairType) {
        log.info("接口调用: POST /api/repair-types, name={}", repairType.getTypeName());
        checkAdmin(request);
        repairTypeService.create(repairType);
        return Result.success();
    }

    /**
     * 修改
     * PUT /api/repair-types/{id}
     */
    @PutMapping("/{id}")
    public Result<?> update(HttpServletRequest request, @PathVariable Long id, @RequestBody RepairType repairType) {
        log.info("接口调用: PUT /api/repair-types/{}, id={}", id);
        checkAdmin(request);
        repairType.setId(id);
        repairTypeService.update(repairType);
        return Result.success();
    }

    /**
     * 删除
     * DELETE /api/repair-types/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: DELETE /api/repair-types/{}, id={}", id);
        checkAdmin(request);
        repairTypeService.delete(id);
        return Result.success();
    }

    /**
     * 启用/禁用（翻转 status）
     * PUT /api/repair-types/toggle/{id}
     */
    @PutMapping("/toggle/{id}")
    public Result<Integer> toggleStatus(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: PUT /api/repair-types/toggle/{}, id={}", id);
        checkAdmin(request);
        Integer newStatus = repairTypeService.toggleStatus(id);
        return Result.success(newStatus);
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
