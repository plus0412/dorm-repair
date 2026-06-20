package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormBuilding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宿舍楼 Mapper 接口
 */
@Mapper
public interface DormBuildingMapper {

    /**
     * 分页查询宿舍楼（支持按名称筛选）
     */
    Page<DormBuilding> selectPageFilter(Page<DormBuilding> page, @Param("name") String name);

    /**
     * 查询所有宿舍楼（下拉列表用）
     */
    List<DormBuilding> selectListAll();

    /**
     * 根据 ID 查询
     */
    DormBuilding selectById(@Param("id") Long id);

    /**
     * 插入
     */
    int insert(DormBuilding building);

    /**
     * 根据 ID 更新
     */
    int updateById(DormBuilding building);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Long id);
}
