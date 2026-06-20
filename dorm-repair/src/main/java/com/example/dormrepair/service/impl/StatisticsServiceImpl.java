package com.example.dormrepair.service.impl;

import com.example.dormrepair.mapper.RepairEvaluationMapper;
import com.example.dormrepair.mapper.RepairOrderMapper;
import com.example.dormrepair.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RepairOrderMapper orderMapper;

    @Autowired
    private RepairEvaluationMapper evaluationMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("total", orderMapper.selectCount(null));
        result.put("pendingAudit", orderMapper.selectCountByStatus(0));
        result.put("repairing", orderMapper.selectCountByStatus(4));
        result.put("completed", orderMapper.selectCountByStatus(5) + orderMapper.selectCountByStatus(6));
        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyData() {
        return orderMapper.selectMonthlyStats();
    }

    @Override
    public List<Map<String, Object>> getTypeData() {
        return orderMapper.selectTypeStats();
    }

    @Override
    public List<Map<String, Object>> getRepairUserRanking() {
        return orderMapper.selectRepairUserRanking();
    }

    @Override
    public List<Map<String, Object>> getSatisfactionData() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int rating = 5; rating >= 1; rating--) {
            Long count = evaluationMapper.selectCountByRating(rating);
            Map<String, Object> item = new HashMap<>();
            item.put("rating", rating);
            item.put("count", count);
            result.add(item);
        }
        return result;
    }
}
