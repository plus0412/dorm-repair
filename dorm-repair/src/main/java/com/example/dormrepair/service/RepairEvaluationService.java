package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairEvaluation;

/**
 * 维修评价业务接口
 */
public interface RepairEvaluationService {

    /**
     * 学生提交评价
     * @param orderId 工单ID
     * @param studentId 学生ID
     * @param rating 评分 1-5
     * @param comment 评价内容
     * @return 评价记录
     */
    RepairEvaluation submitEvaluation(Long orderId, Long studentId, Integer rating, String comment);

    /**
     * 查询工单的评价
     */
    RepairEvaluation getEvaluationByOrderId(Long orderId);

    /**
     * 分页查询所有评价（管理员）
     */
    Page<RepairEvaluation> getPage(Page<RepairEvaluation> page, Integer rating, String keyword);

    /**
     * 维修人员查询自己收到的评价
     */
    Page<RepairEvaluation> getMyEvaluations(Page<RepairEvaluation> page, Long repairUserId);
}
