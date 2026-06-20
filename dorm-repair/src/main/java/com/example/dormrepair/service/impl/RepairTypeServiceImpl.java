package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairType;
import com.example.dormrepair.mapper.RepairTypeMapper;
import com.example.dormrepair.service.RepairTypeService;
import com.example.dormrepair.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepairTypeServiceImpl implements RepairTypeService {

    private static final Logger log = LoggerFactory.getLogger(RepairTypeServiceImpl.class);

    @Autowired
    private RepairTypeMapper repairTypeMapper;

    @Override
    public Page<RepairType> selectPageFilter(Page<RepairType> page, String name, Integer status) {
        log.debug("分页查询报修类型: name={}, status={}", name, status);
        return repairTypeMapper.selectPageFilter(page, name, status);
    }

    @Override
    public List<RepairType> selectListEnabled() {
        log.debug("查询启用状态的报修类型列表");
        return repairTypeMapper.selectListEnabled();
    }

    @Override
    public RepairType getById(Long id) {
        log.debug("查询报修类型详情: id={}", id);
        RepairType repairType = repairTypeMapper.selectById(id);
        if (repairType == null) {
            log.warn("报修类型不存在: id={}", id);
            throw new BusinessException("报修类型不存在");
        }
        return repairType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RepairType repairType) {
        log.info("新增报修类型: name={}", repairType.getTypeName());
        repairType.setStatus(repairType.getStatus() == null ? 1 : repairType.getStatus());
        repairType.setCreateTime(LocalDateTime.now());
        repairType.setUpdateTime(LocalDateTime.now());
        repairTypeMapper.insert(repairType);
        log.info("新增报修类型成功: id={}", repairType.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RepairType repairType) {
        log.info("修改报修类型: id={}", repairType.getId());
        RepairType exist = repairTypeMapper.selectById(repairType.getId());
        if (exist == null) {
            log.warn("修改失败，报修类型不存在: id={}", repairType.getId());
            throw new BusinessException("报修类型不存在");
        }
        repairType.setUpdateTime(LocalDateTime.now());
        repairTypeMapper.updateById(repairType);
        log.info("修改报修类型成功: id={}", repairType.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除报修类型: id={}", id);
        RepairType exist = repairTypeMapper.selectById(id);
        if (exist == null) {
            log.warn("删除失败，报修类型不存在: id={}", id);
            throw new BusinessException("报修类型不存在");
        }
        repairTypeMapper.deleteById(id);
        log.info("删除报修类型成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer toggleStatus(Long id) {
        log.info("翻转报修类型状态: id={}", id);
        RepairType repairType = repairTypeMapper.selectById(id);
        if (repairType == null) {
            log.warn("翻转状态失败，报修类型不存在: id={}", id);
            throw new BusinessException("报修类型不存在");
        }
        int newStatus = (repairType.getStatus() == null || repairType.getStatus() == 0) ? 1 : 0;
        RepairType update = new RepairType();
        update.setId(id);
        update.setStatus(newStatus);
        repairTypeMapper.updateById(update);
        log.info("报修类型状态翻转成功: id={}, newStatus={}", id, newStatus);
        return newStatus;
    }
}
