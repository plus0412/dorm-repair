package com.example.dormrepair.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公告 Mapper 接口
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 分页查询公告（管理员用，可按状态筛选）
     */
    Page<Notice> selectPageFilter(Page<Notice> page, @Param("status") Integer status);

    /**
     * 查询显示中的公告列表（所有角色用）
     */
    Page<Notice> selectShowList(Page<Notice> page);
}
