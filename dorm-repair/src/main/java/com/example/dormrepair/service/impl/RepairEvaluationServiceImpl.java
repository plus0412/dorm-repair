package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.entity.RepairEvaluation;
import com.example.dormrepair.entity.RepairOrder;
import com.example.dormrepair.mapper.RepairEvaluationMapper;
import com.example.dormrepair.mapper.RepairOrderMapper;
import com.example.dormrepair.service.RepairEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 维修评价业务实现
 */
@Service
public class RepairEvaluationServiceImpl implements RepairEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(RepairEvaluationServiceImpl.class);

    @Autowired
    private RepairEvaluationMapper evaluationMapper;

    @Autowired
    private RepairOrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepairEvaluation submitEvaluation(Long orderId, Long studentId, Integer rating, String comment) {
        log.info("学生提交评价: orderId={}, studentId={}, rating={}", orderId, studentId, rating);

        // 校验工单存在
        RepairOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        // 校验是否是该学生提交的工单
        if (!order.getStudentId().equals(studentId)) {
            throw new BusinessException("只能评价自己的报修工单");
        }

        // 校验是否已评价（状态为6说明已评价）
        if (order.getStatus() == 6) {
            throw new BusinessException("该工单已评价，请勿重复提交");
        }

        // 校验工单状态是否为已完成(5)
        if (order.getStatus() != 5) {
            throw new BusinessException("只能评价已完成的工单");
        }

        // 校验评分范围
        if (rating == null || rating < 1 || rating > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        // 创建评价记录
        RepairEvaluation evaluation = new RepairEvaluation();
        evaluation.setOrderId(orderId);
        evaluation.setStudentId(studentId);
        evaluation.setRepairUserId(order.getRepairUserId());
        evaluation.setRating(rating);
        evaluation.setComment(comment);
        evaluation.setCreateTime(LocalDateTime.now());

        evaluationMapper.insert(evaluation);
        log.info("评价保存成功: evaluationId={}", evaluation.getId());

        // 更新工单状态为已评价(6)，并记录评价ID
        order.setStatus(6);
        order.setEvaluateId(evaluation.getId());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("工单状态更新为已评价: orderId={}", orderId);

        return evaluation;
    }

    @Override
    public RepairEvaluation getEvaluationByOrderId(Long orderId) {
        LambdaQueryWrapper<RepairEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairEvaluation::getOrderId, orderId);
        return evaluationMapper.selectOne(wrapper);
    }

    @Override
    public Page<RepairEvaluation> getPage(Page<RepairEvaluation> page, Integer rating, String keyword) {
        // 使用关联查询获取学生姓名和维修人员姓名
        return evaluationMapper.selectPageWithNames(page, rating, keyword);
    }

    @Override
    public Page<RepairEvaluation> getMyEvaluations(Page<RepairEvaluation> page, Long repairUserId) {
        LambdaQueryWrapper<RepairEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairEvaluation::getRepairUserId, repairUserId);
        wrapper.orderByDesc(RepairEvaluation::getCreateTime);
        return evaluationMapper.selectPage(page, wrapper);
    }
}
