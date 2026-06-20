package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.entity.Notice;
import com.example.dormrepair.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    /**
     * 分页查询公告（管理员）
     * GET /api/notices/page?pageNum=1&pageSize=10&status=1
     */
    @GetMapping("/page")
    public Result<Page<Notice>> page(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        String role = (String) request.getAttribute("role");
        log.info("分页查询公告: role={}, status={}", role, status);

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以分页查询所有公告");
        }

        Page<Notice> page = new Page<>(pageNum, pageSize);
        Page<Notice> result = noticeService.getPage(page, status);
        return Result.success(result);
    }

    /**
     * 查询显示中的公告列表（所有角色）
     * GET /api/notices/list?pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public Result<Page<Notice>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询显示中的公告列表");
        Page<Notice> page = new Page<>(pageNum, pageSize);
        Page<Notice> result = noticeService.getShowList(page);
        return Result.success(result);
    }

    /**
     * 查询公告详情
     * GET /api/notices/{id}
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        log.info("查询公告详情: id={}", id);
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

    /**
     * 新增公告（管理员）
     * POST /api/notices
     */
    @PostMapping
    public Result<Notice> create(
            HttpServletRequest request,
            @RequestBody Notice notice) {
        Long publisherId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        log.info("新增公告: publisherId={}, title={}", publisherId, notice.getTitle());

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以发布公告");
        }

        Notice created = noticeService.create(notice, publisherId);
        return Result.success(created);
    }

    /**
     * 修改公告（管理员）
     * PUT /api/notices/{id}
     */
    @PutMapping("/{id}")
    public Result<?> update(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Notice notice) {
        String role = (String) request.getAttribute("role");
        log.info("修改公告: id={}, title={}", id, notice.getTitle());

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以修改公告");
        }

        notice.setId(id);
        noticeService.update(notice);
        return Result.success();
    }

    /**
     * 删除公告（管理员）
     * DELETE /api/notices/{id}
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(
            HttpServletRequest request,
            @PathVariable Long id) {
        String role = (String) request.getAttribute("role");
        log.info("删除公告: id={}", id);

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以删除公告");
        }

        noticeService.delete(id);
        return Result.success();
    }

    /**
     * 修改显示状态（管理员）
     * PUT /api/notices/status/{id}?status=1
     */
    @PutMapping("/status/{id}")
    public Result<?> updateStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam Integer status) {
        String role = (String) request.getAttribute("role");
        log.info("修改公告状态: id={}, status={}", id, status);

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以修改公告状态");
        }

        noticeService.updateStatus(id, status);
        return Result.success();
    }
}
