package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 维修记录 Mapper 接口
 */
@Mapper
public interface RepairRecordMapper extends BaseMapper<RepairRecord> {

    /**
     * 分页查询某工单的操作记录
     */
    Page<RepairRecord> selectPageByOrderId(Page<RepairRecord> page, @Param("orderId") Long orderId);

    /**
     * 分页查询某维修人员的历史记录
     */
    Page<RepairRecord> selectPageByRepairUserId(Page<RepairRecord> page, @Param("repairUserId") Long repairUserId);
}
