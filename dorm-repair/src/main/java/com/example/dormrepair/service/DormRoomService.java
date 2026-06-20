package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormRoom;

import java.util.List;

/**
 * 宿舍房间业务接口
 */
public interface DormRoomService {

    /**
     * 分页查询（支持按宿舍楼ID、房间号筛选）
     */
    Page<DormRoom> selectPageFilter(Page<DormRoom> page, Long buildingId, String roomNo);

    /**
     * 查询房间列表（下拉用，可按 buildingId 筛选）
     */
    List<DormRoom> selectListByBuildingId(Long buildingId);

    /**
     * 根据 ID 查询
     */
    DormRoom getById(Long id);

    /**
     * 新增
     */
    void create(DormRoom room);

    /**
     * 修改
     */
    void update(DormRoom room);

    /**
     * 删除
     */
    void delete(Long id);
}
