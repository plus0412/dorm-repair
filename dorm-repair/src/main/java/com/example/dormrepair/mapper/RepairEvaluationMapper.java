package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 维修评价 Mapper 接口
 */
@Mapper
public interface RepairEvaluationMapper extends BaseMapper<RepairEvaluation> {

    /**
     * 按评分统计数量
     */
    Long selectCountByRating(@Param("rating") Integer rating);

    /**
     * 分页查询评价（关联学生姓名和维修人员姓名）
     */
    Page<RepairEvaluation> selectPageWithNames(Page<RepairEvaluation> page,
                                               @Param("rating") Integer rating,
                                               @Param("keyword") String keyword);
}
