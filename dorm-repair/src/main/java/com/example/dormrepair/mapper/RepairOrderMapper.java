package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dormrepair.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 报修工单 Mapper 接口
 */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {

    /**
     * 分页查询工单（支持按状态、报修类型、学生姓名筛选）
     */
    Page<RepairOrder> selectPageFilter(Page<RepairOrder> page,
                                       @Param("studentId") Long studentId,
                                       @Param("repairUserId") Long repairUserId,
                                       @Param("status") Integer status,
                                       @Param("statusList") List<Integer> statusList,
                                       @Param("repairTypeId") Long repairTypeId,
                                       @Param("keyword") String keyword);

    /**
     * 根据 ID 查询工单详情（关联学生姓名、宿舍楼、房间、报修类型名称）
     */
    RepairOrder selectDetailById(@Param("id") Long id);

    /**
     * 根据 orderNo 查询
     */
    RepairOrder selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 按状态统计数量
     */
    Long selectCountByStatus(@Param("status") Integer status);

    /**
     * 每月报修数量统计（近12个月）
     */
    List<Map<String, Object>> selectMonthlyStats();

    /**
     * 各类型报修数量统计
     */
    List<Map<String, Object>> selectTypeStats();

    /**
     * 维修人员完成数量排行
     */
    List<Map<String, Object>> selectRepairUserRanking();
}
