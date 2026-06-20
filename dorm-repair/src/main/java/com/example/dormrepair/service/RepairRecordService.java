package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairRecord;

/**
 * 维修记录业务接口
 */
public interface RepairRecordService {

    /**
     * 分页查询某工单的操作记录
     */
    Page<RepairRecord> getRecordsByOrderId(Page<RepairRecord> page, Long orderId);

    /**
     * 维修人员分页查询自己的历史记录
     */
    Page<RepairRecord> getMyRecords(Page<RepairRecord> page, Long repairUserId);
}
