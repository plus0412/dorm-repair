package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.Notice;

/**
 * 公告业务接口
 */
public interface NoticeService {

    /**
     * 分页查询公告（管理员用）
     */
    Page<Notice> getPage(Page<Notice> page, Integer status);

    /**
     * 查询显示中的公告列表（所有角色）
     */
    Page<Notice> getShowList(Page<Notice> page);

    /**
     * 查询公告详情
     */
    Notice getById(Long id);

    /**
     * 新增公告（管理员）
     */
    Notice create(Notice notice, Long publisherId);

    /**
     * 修改公告（管理员）
     */
    void update(Notice notice);

    /**
     * 删除公告（管理员）
     */
    void delete(Long id);

    /**
     * 修改显示状态（管理员）
     */
    void updateStatus(Long id, Integer status);
}
