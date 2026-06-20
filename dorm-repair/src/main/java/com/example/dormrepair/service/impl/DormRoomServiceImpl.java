package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormRoom;
import com.example.dormrepair.mapper.DormRoomMapper;
import com.example.dormrepair.service.DormRoomService;
import com.example.dormrepair.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DormRoomServiceImpl implements DormRoomService {

    private static final Logger log = LoggerFactory.getLogger(DormRoomServiceImpl.class);

    @Autowired
    private DormRoomMapper dormRoomMapper;

    @Override
    public Page<DormRoom> selectPageFilter(Page<DormRoom> page, Long buildingId, String roomNo) {
        log.debug("分页查询房间: buildingId={}, roomNo={}", buildingId, roomNo);
        return dormRoomMapper.selectPageFilter(page, buildingId, roomNo);
    }

    @Override
    public List<DormRoom> selectListByBuildingId(Long buildingId) {
        log.debug("按宿舍楼查询房间列表: buildingId={}", buildingId);
        return dormRoomMapper.selectListByBuildingId(buildingId);
    }

    @Override
    public DormRoom getById(Long id) {
        log.debug("查询房间详情: id={}", id);
        DormRoom room = dormRoomMapper.selectById(id);
        if (room == null) {
            log.warn("房间不存在: id={}", id);
            throw new BusinessException("房间不存在");
        }
        return room;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DormRoom room) {
        log.info("新增房间: buildingId={}, roomNo={}", room.getBuildingId(), room.getRoomNo());
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        dormRoomMapper.insert(room);
        log.info("新增房间成功: id={}", room.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DormRoom room) {
        log.info("修改房间: id={}", room.getId());
        DormRoom exist = dormRoomMapper.selectById(room.getId());
        if (exist == null) {
            log.warn("修改失败，房间不存在: id={}", room.getId());
            throw new BusinessException("房间不存在");
        }
        room.setUpdateTime(LocalDateTime.now());
        dormRoomMapper.updateById(room);
        log.info("修改房间成功: id={}", room.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除房间: id={}", id);
        DormRoom exist = dormRoomMapper.selectById(id);
        if (exist == null) {
            log.warn("删除失败，房间不存在: id={}", id);
            throw new BusinessException("房间不存在");
        }
        dormRoomMapper.deleteById(id);
        log.info("删除房间成功: id={}", id);
    }
}
