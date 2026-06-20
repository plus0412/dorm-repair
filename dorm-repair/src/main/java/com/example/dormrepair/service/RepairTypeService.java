package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairType;

import java.util.List;

/**
 * 报修类型业务接口
 */
public interface RepairTypeService {

    /**
     * 分页查询（支持按名称、状态筛选）
     */
    Page<RepairType> selectPageFilter(Page<RepairType> page, String name, Integer status);

    /**
     * 查询启用状态的类型列表（下拉用）
     */
    List<RepairType> selectListEnabled();

    /**
     * 根据 ID 查询
     */
    RepairType getById(Long id);

    /**
     * 新增
     */
    void create(RepairType repairType);

    /**
     * 修改
     */
    void update(RepairType repairType);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 启用/禁用（翻转 status）
     * @return 翻转后的 status 值
     */
    Integer toggleStatus(Long id);
}
