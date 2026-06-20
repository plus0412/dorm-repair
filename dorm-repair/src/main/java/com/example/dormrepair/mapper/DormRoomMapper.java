package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍房间 Mapper 接口
 */
@Mapper
public interface DormRoomMapper {

    /**
     * 分页查询宿舍房间（支持按宿舍楼ID、房间号筛选）
     */
    Page<DormRoom> selectPageFilter(Page<DormRoom> page,
                                   @Param("buildingId") Long buildingId,
                                   @Param("roomNo") String roomNo);

    /**
     * 查询房间列表（下拉用，可按 buildingId 筛选）
     */
    List<DormRoom> selectListByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * 根据 ID 查询
     */
    DormRoom selectById(@Param("id") Long id);

    /**
     * 插入
     */
    int insert(DormRoom room);

    /**
     * 根据 ID 更新
     */
    int updateById(DormRoom room);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Long id);
}
