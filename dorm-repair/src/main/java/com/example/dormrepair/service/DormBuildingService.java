package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormBuilding;

import java.util.List;

/**
 * 宿舍楼业务接口
 */
public interface DormBuildingService {

    /**
     * 分页查询（支持按名称筛选）
     */
    Page<DormBuilding> selectPageFilter(Page<DormBuilding> page, String name);

    /**
     * 查询所有（下拉列表用）
     */
    List<DormBuilding> selectListAll();

    /**
     * 根据 ID 查询
     */
    DormBuilding getById(Long id);

    /**
     * 新增
     */
    void create(DormBuilding building);

    /**
     * 修改
     */
    void update(DormBuilding building);

    /**
     * 删除
     */
    void delete(Long id);
}
