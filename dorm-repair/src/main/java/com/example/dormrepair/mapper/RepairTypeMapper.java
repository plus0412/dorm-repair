package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报修类型 Mapper 接口
 */
@Mapper
public interface RepairTypeMapper {

    /**
     * 分页查询报修类型（支持按名称、状态筛选）
     */
    Page<RepairType> selectPageFilter(Page<RepairType> page,
                                     @Param("name") String name,
                                     @Param("status") Integer status);

    /**
     * 查询启用状态的报修类型列表（下拉用）
     */
    List<RepairType> selectListEnabled();

    /**
     * 根据 ID 查询
     */
    RepairType selectById(@Param("id") Long id);

    /**
     * 插入
     */
    int insert(RepairType repairType);

    /**
     * 根据 ID 更新
     */
    int updateById(RepairType repairType);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Long id);
}
