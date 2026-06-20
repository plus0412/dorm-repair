package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.DormBuilding;
import com.example.dormrepair.mapper.DormBuildingMapper;
import com.example.dormrepair.service.DormBuildingService;
import com.example.dormrepair.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DormBuildingServiceImpl implements DormBuildingService {

    private static final Logger log = LoggerFactory.getLogger(DormBuildingServiceImpl.class);

    @Autowired
    private DormBuildingMapper dormBuildingMapper;

    @Override
    public Page<DormBuilding> selectPageFilter(Page<DormBuilding> page, String name) {
        log.debug("分页查询宿舍楼: name={}", name);
        return dormBuildingMapper.selectPageFilter(page, name);
    }

    @Override
    public List<DormBuilding> selectListAll() {
        log.debug("查询所有宿舍楼列表");
        return dormBuildingMapper.selectListAll();
    }

    @Override
    public DormBuilding getById(Long id) {
        log.debug("查询宿舍楼详情: id={}", id);
        DormBuilding building = dormBuildingMapper.selectById(id);
        if (building == null) {
            log.warn("宿舍楼不存在: id={}", id);
            throw new BusinessException("宿舍楼不存在");
        }
        return building;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DormBuilding building) {
        log.info("新增宿舍楼: name={}", building.getName());
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        dormBuildingMapper.insert(building);
        log.info("新增宿舍楼成功: id={}", building.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DormBuilding building) {
        log.info("修改宿舍楼: id={}", building.getId());
        DormBuilding exist = dormBuildingMapper.selectById(building.getId());
        if (exist == null) {
            log.warn("修改失败，宿舍楼不存在: id={}", building.getId());
            throw new BusinessException("宿舍楼不存在");
        }
        building.setUpdateTime(LocalDateTime.now());
        dormBuildingMapper.updateById(building);
        log.info("修改宿舍楼成功: id={}", building.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除宿舍楼: id={}", id);
        DormBuilding exist = dormBuildingMapper.selectById(id);
        if (exist == null) {
            log.warn("删除失败，宿舍楼不存在: id={}", id);
            throw new BusinessException("宿舍楼不存在");
        }
        dormBuildingMapper.deleteById(id);
        log.info("删除宿舍楼成功: id={}", id);
    }
}
