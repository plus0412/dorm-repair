package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormBuilding;
import com.example.dormrepair.service.DormBuildingService;
import com.example.dormrepair.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 宿舍楼管理（管理员）
 * 路径前缀：/api/buildings
 */
@RestController
@RequestMapping("/api/buildings")
public class DormBuildingController {

    private static final Logger log = LoggerFactory.getLogger(DormBuildingController.class);

    @Autowired
    private DormBuildingService dormBuildingService;

    /**
     * 分页查询
     * GET /api/buildings/page?pageNum=1&pageSize=10&name=xxx
     */
    @GetMapping("/page")
    public Result<Page<DormBuilding>> page(HttpServletRequest request,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(required = false) String name) {
        log.info("接口调用: GET /api/buildings/page");
        checkAdmin(request);
        Page<DormBuilding> page = new Page<>(pageNum, pageSize);
        Page<DormBuilding> result = dormBuildingService.selectPageFilter(page, name);
        return Result.success(result);
    }

    /**
     * 列表查询（下拉用）
     * GET /api/buildings/list
     * 所有登录用户都可访问（用于报修表单选择）
     */
    @GetMapping("/list")
    public Result<List<DormBuilding>> list(HttpServletRequest request) {
        log.info("接口调用: GET /api/buildings/list");
        // 不需要管理员权限，所有登录用户都可访问
        List<DormBuilding> list = dormBuildingService.selectListAll();
        return Result.success(list);
    }

    /**
     * 根据 ID 查询
     * GET /api/buildings/{id}
     */
    @GetMapping("/{id}")
    public Result<DormBuilding> getById(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: GET /api/buildings/{}, id={}", id);
        checkAdmin(request);
        DormBuilding building = dormBuildingService.getById(id);
        return Result.success(building);
    }

    /**
     * 新增
     * POST /api/buildings
     */
    @PostMapping
    public Result<?> create(HttpServletRequest request, @RequestBody DormBuilding building) {
        log.info("接口调用: POST /api/buildings, name={}", building.getName());
        checkAdmin(request);
        dormBuildingService.create(building);
        return Result.success();
    }

    /**
     * 修改
     * PUT /api/buildings/{id}
     */
    @PutMapping("/{id}")
    public Result<?> update(HttpServletRequest request, @PathVariable Long id, @RequestBody DormBuilding building) {
        log.info("接口调用: PUT /api/buildings/{}, id={}", id);
        checkAdmin(request);
        building.setId(id);
        dormBuildingService.update(building);
        return Result.success();
    }

    /**
     * 删除
     * DELETE /api/buildings/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: DELETE /api/buildings/{}, id={}", id);
        checkAdmin(request);
        dormBuildingService.delete(id);
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
