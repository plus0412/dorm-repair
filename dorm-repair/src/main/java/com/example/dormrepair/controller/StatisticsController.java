package com.example.dormrepair.controller;

import com.example.dormrepair.common.result.Result;
import com.example.dormrepair.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取概览数据
     * GET /api/statistics/overview
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        log.info("获取统计数据概览");
        Map<String, Object> data = statisticsService.getOverview();
        return Result.success(data);
    }

    /**
     * 获取每月报修数量（近12个月）
     * GET /api/statistics/monthly
     */
    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyData() {
        log.info("获取每月报修统计数据");
        List<Map<String, Object>> data = statisticsService.getMonthlyData();
        return Result.success(data);
    }

    /**
     * 获取各类型报修数量占比
     * GET /api/statistics/type
     */
    @GetMapping("/type")
    public Result<List<Map<String, Object>>> getTypeData() {
        log.info("获取报修类型统计数据");
        List<Map<String, Object>> data = statisticsService.getTypeData();
        return Result.success(data);
    }

    /**
     * 获取维修人员完成数量排行
     * GET /api/statistics/repair-user
     */
    @GetMapping("/repair-user")
    public Result<List<Map<String, Object>>> getRepairUserRanking() {
        log.info("获取维修人员排行数据");
        List<Map<String, Object>> data = statisticsService.getRepairUserRanking();
        return Result.success(data);
    }

    /**
     * 获取维修满意度统计（各分段人数）
     * GET /api/statistics/satisfaction
     */
    @GetMapping("/satisfaction")
    public Result<List<Map<String, Object>>> getSatisfactionData() {
        log.info("获取维修满意度数据");
        List<Map<String, Object>> data = statisticsService.getSatisfactionData();
        return Result.success(data);
    }
}
