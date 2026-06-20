package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormRoom;
import com.example.dormrepair.service.DormRoomService;
import com.example.dormrepair.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 宿舍房间管理（管理员）
 * 路径前缀：/api/rooms
 */
@RestController
@RequestMapping("/api/rooms")
public class DormRoomController {

    private static final Logger log = LoggerFactory.getLogger(DormRoomController.class);

    @Autowired
    private DormRoomService dormRoomService;

    /**
     * 分页查询
     * GET /api/rooms/page?pageNum=1&pageSize=10&buildingId=&roomNo=
     */
    @GetMapping("/page")
    public Result<Page<DormRoom>> page(HttpServletRequest request,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(required = false) Long buildingId,
                                           @RequestParam(required = false) String roomNo) {
        log.info("接口调用: GET /api/rooms/page");
        checkAdmin(request);
        Page<DormRoom> page = new Page<>(pageNum, pageSize);
        Page<DormRoom> result = dormRoomService.selectPageFilter(page, buildingId, roomNo);
        return Result.success(result);
    }

    /**
     * 列表查询（下拉用）
     * GET /api/rooms/list?buildingId=
     * 所有登录用户都可访问（用于报修表单选择）
     */
    @GetMapping("/list")
    public Result<List<DormRoom>> list(HttpServletRequest request,
                                           @RequestParam(required = false) Long buildingId) {
        log.info("接口调用: GET /api/rooms/list, buildingId={}", buildingId);
        // 不需要管理员权限，所有登录用户都可访问
        List<DormRoom> list = dormRoomService.selectListByBuildingId(buildingId);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询
     * GET /api/rooms/{id}
     */
    @GetMapping("/{id}")
    public Result<DormRoom> getById(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: GET /api/rooms/{}, id={}", id);
        checkAdmin(request);
        DormRoom room = dormRoomService.getById(id);
        return Result.success(room);
    }

    /**
     * 新增
     * POST /api/rooms
     */
    @PostMapping
    public Result<?> create(HttpServletRequest request, @RequestBody DormRoom room) {
        log.info("接口调用: POST /api/rooms, buildingId={}, roomNo={}", room.getBuildingId(), room.getRoomNo());
        checkAdmin(request);
        dormRoomService.create(room);
        return Result.success();
    }

    /**
     * 修改
     * PUT /api/rooms/{id}
     */
    @PutMapping("/{id}")
    public Result<?> update(HttpServletRequest request, @PathVariable Long id, @RequestBody DormRoom room) {
        log.info("接口调用: PUT /api/rooms/{}, id={}", id);
        checkAdmin(request);
        room.setId(id);
        dormRoomService.update(room);
        return Result.success();
    }

    /**
     * 删除
     * DELETE /api/rooms/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        log.info("接口调用: DELETE /api/rooms/{}, id={}", id);
        checkAdmin(request);
        dormRoomService.delete(id);
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
