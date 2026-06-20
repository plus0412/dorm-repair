package com.example.dormrepair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.entity.RepairEvaluation;
import com.example.dormrepair.service.RepairEvaluationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 维修评价控制器
 */
@RestController
@RequestMapping("/api/evaluations")
public class RepairEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(RepairEvaluationController.class);

    @Autowired
    private RepairEvaluationService evaluationService;

    /**
     * 学生提交评价
     * POST /api/evaluations
     */
    @PostMapping
    public Result<RepairEvaluation> submit(
            HttpServletRequest request,
            @RequestBody EvaluationSubmitDTO dto) {
        Long studentId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        log.info("学生提交评价: studentId={}, orderId={}, rating={}", studentId, dto.getOrderId(), dto.getRating());

        if (!"student".equals(role)) {
            return Result.error(403, "只有学生可以提交评价");
        }

        RepairEvaluation evaluation = evaluationService.submitEvaluation(
                dto.getOrderId(), studentId, dto.getRating(), dto.getComment());
        return Result.success(evaluation);
    }

    /**
     * 查询工单的评价
     * GET /api/evaluations/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    public Result<RepairEvaluation> getByOrderId(@PathVariable Long orderId) {
        log.info("查询工单评价: orderId={}", orderId);
        RepairEvaluation evaluation = evaluationService.getEvaluationByOrderId(orderId);
        return Result.success(evaluation);
    }

    /**
     * 分页查询所有评价（管理员）
     * GET /api/evaluations/page?pageNum=1&pageSize=10&rating=5&keyword=xxx
     */
    @GetMapping("/page")
    public Result<Page<RepairEvaluation>> page(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String keyword) {
        String role = (String) request.getAttribute("role");
        log.info("分页查询评价: role={}, rating={}, keyword={}", role, rating, keyword);

        if (!"admin".equals(role)) {
            return Result.error(403, "只有管理员可以查看所有评价");
        }

        Page<RepairEvaluation> page = new Page<>(pageNum, pageSize);
        Page<RepairEvaluation> result = evaluationService.getPage(page, rating, keyword);
        return Result.success(result);
    }

    /**
     * 维修人员查询自己的评价
     * GET /api/evaluations/my?pageNum=1&pageSize=10
     */
    @GetMapping("/my")
    public Result<Page<RepairEvaluation>> getMyEvaluations(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        log.info("维修人员查询自己评价: userId={}, role={}", userId, role);

        if (!"repair".equals(role)) {
            return Result.error(403, "只有维修人员可以查询自己的评价");
        }

        Page<RepairEvaluation> page = new Page<>(pageNum, pageSize);
        Page<RepairEvaluation> result = evaluationService.getMyEvaluations(page, userId);
        return Result.success(result);
    }

    /**
     * 评价提交 DTO
     */
    public static class EvaluationSubmitDTO {
        private Long orderId;
        private Integer rating;
        private String comment;

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}
