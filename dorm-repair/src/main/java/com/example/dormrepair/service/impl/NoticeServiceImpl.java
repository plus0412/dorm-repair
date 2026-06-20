package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.entity.Notice;
import com.example.dormrepair.mapper.NoticeMapper;
import com.example.dormrepair.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 公告业务实现
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public Page<Notice> getPage(Page<Notice> page, Integer status) {
        return noticeMapper.selectPageFilter(page, status);
    }

    @Override
    public Page<Notice> getShowList(Page<Notice> page) {
        return noticeMapper.selectShowList(page);
    }

    @Override
    public Notice getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice create(Notice notice, Long publisherId) {
        notice.setPublisherId(publisherId);
        notice.setStatus(1); // 默认显示
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        log.info("公告创建成功: id={}, title={}", notice.getId(), notice.getTitle());
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Notice notice) {
        Notice existing = noticeMapper.selectById(notice.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        log.info("公告更新成功: id={}", notice.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Notice existing = noticeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.deleteById(id);
        log.info("公告删除成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Notice existing = noticeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(existing);
        log.info("公告状态更新: id={}, status={}", id, status);
    }
}
