package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.entity.RepairRecord;
import com.example.dormrepair.service.RepairRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 维修记录控制器
 */
@RestController
@RequestMapping("/api/repair-records")
public class RepairRecordController {

    private static final Logger log = LoggerFactory.getLogger(RepairRecordController.class);

    @Autowired
    private RepairRecordService repairRecordService;

    /**
     * 查询某工单的操作记录
     * GET /api/repair-records/order/{orderId}?pageNum=1&pageSize=10
     */
    @GetMapping("/order/{orderId}")
    public Result<Page<RepairRecord>> getByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询工单操作记录: orderId={}", orderId);
        Page<RepairRecord> page = new Page<>(pageNum, pageSize);
        Page<RepairRecord> result = repairRecordService.getRecordsByOrderId(page, orderId);
        return Result.success(result);
    }

    /**
     * 维修人员查询自己的历史记录
     * GET /api/repair-records/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    public Result<Page<RepairRecord>> getMyRecords(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        log.info("查询维修人员历史记录: userId={}, role={}", userId, role);

        if (!"repair".equals(role)) {
            return Result.error(403, "只有维修人员可以查询历史记录");
        }

        Page<RepairRecord> page = new Page<>(pageNum, pageSize);
        Page<RepairRecord> result = repairRecordService.getMyRecords(page, userId);
        return Result.success(result);
    }
}
