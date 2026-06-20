package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairRecord;
import com.example.dormrepair.mapper.RepairRecordMapper;
import com.example.dormrepair.service.RepairRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 维修记录业务实现
 */
@Service
public class RepairRecordServiceImpl implements RepairRecordService {

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Override
    public Page<RepairRecord> getRecordsByOrderId(Page<RepairRecord> page, Long orderId) {
        return repairRecordMapper.selectPageByOrderId(page, orderId);
    }

    @Override
    public Page<RepairRecord> getMyRecords(Page<RepairRecord> page, Long repairUserId) {
        return repairRecordMapper.selectPageByRepairUserId(page, repairUserId);
    }
}
