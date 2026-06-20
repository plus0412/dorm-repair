package com.example.dormrepair.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计业务接口
 */
public interface StatisticsService {

    /**
     * 获取概览数据
     * @return { total: 总数, pendingAudit: 待审核, repairing: 维修中, completed: 已完成 }
     */
    Map<String, Object> getOverview();

    /**
     * 获取每月报修数量（近12个月）
     * @return [{ month: "2025-05", count: 10 }, ...]
     */
    List<Map<String, Object>> getMonthlyData();

    /**
     * 获取各类型报修数量占比
     * @return [{ typeName: "水电维修", count: 20 }, ...]
     */
    List<Map<String, Object>> getTypeData();

    /**
     * 获取维修人员完成数量排行
     * @return [{ repairUserName: "张三", count: 15 }, ...]
     */
    List<Map<String, Object>> getRepairUserRanking();

    /**
     * 获取维修满意度统计（各分段人数）
     * @return [{ rating: 5, count: 10 }, { rating: 4, count: 5 }, ...]
     */
    List<Map<String, Object>> getSatisfactionData();
}
