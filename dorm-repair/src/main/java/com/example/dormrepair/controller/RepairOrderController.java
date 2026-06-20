package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.entity.RepairOrder;
import com.example.dormrepair.service.RepairOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repair-orders")
public class RepairOrderController {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderController.class);

    @Autowired
    private RepairOrderService repairOrderService;

    @PostMapping
    public Result<RepairOrder> submit(HttpServletRequest request,
                                      @RequestPart("order") RepairOrder order,
                                      @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long userId = (Long) request.getAttribute("userId");
        order.setStudentId(userId);
        return Result.success(repairOrderService.submitOrder(order, images));
    }

    @PutMapping("/resubmit/{id}")
    public Result<?> resubmit(HttpServletRequest request,
                              @PathVariable Long id,
                              @RequestPart("order") RepairOrder order,
                              @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long userId = (Long) request.getAttribute("userId");
        order.setId(id);
        order.setStudentId(userId);
        repairOrderService.resubmitOrder(order, images);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<Page<RepairOrder>> page(HttpServletRequest request,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) String statusList,
                                          @RequestParam(required = false) Long repairTypeId,
                                          @RequestParam(required = false) String keyword) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        List<Integer> statuses = parseStatusList(statusList);

        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        Page<RepairOrder> result = repairOrderService.getPage(
                page, userId, role, null, status, statuses, repairTypeId, keyword);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Page<RepairOrder>> getMyOrders(HttpServletRequest request,
                                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        return Result.success(repairOrderService.getMyOrders(page, userId, role));
    }

    @GetMapping("/{id}")
    public Result<RepairOrder> getDetail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        return Result.success(repairOrderService.getDetail(id, userId, role));
    }

    @PutMapping("/cancel/{id}")
    public Result<?> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        repairOrderService.cancelOrder(id, userId);
        return Result.success();
    }

    @PutMapping("/audit/{id}")
    public Result<?> audit(HttpServletRequest request,
                           @PathVariable Long id,
                           @RequestParam boolean approved,
                           @RequestParam(required = false) String auditRemark) {
        Long adminId = (Long) request.getAttribute("userId");
        repairOrderService.auditOrder(id, approved, auditRemark, adminId);
        return Result.success();
    }

    @PutMapping("/assign/{id}")
    public Result<?> assign(HttpServletRequest request,
                            @PathVariable Long id,
                            @RequestParam Long repairUserId) {
        Long adminId = (Long) request.getAttribute("userId");
        repairOrderService.assignOrder(id, repairUserId, adminId);
        return Result.success();
    }

    @PutMapping("/start/{id}")
    public Result<?> startRepair(HttpServletRequest request, @PathVariable Long id) {
        Long repairUserId = (Long) request.getAttribute("userId");
        repairOrderService.startRepair(id, repairUserId);
        return Result.success();
    }

    @PutMapping("/finish/{id}")
    public Result<?> finishRepair(HttpServletRequest request,
                                  @PathVariable Long id,
                                  @RequestParam(required = false) String finishRemark) {
        Long repairUserId = (Long) request.getAttribute("userId");
        repairOrderService.finishRepair(id, repairUserId, finishRemark);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id) {
        Long adminId = (Long) request.getAttribute("userId");
        repairOrderService.deleteOrder(id, adminId);
        return Result.success();
    }

    private List<Integer> parseStatusList(String statusList) {
        if (statusList == null || statusList.isBlank()) {
            return null;
        }
        return Arrays.stream(statusList.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
