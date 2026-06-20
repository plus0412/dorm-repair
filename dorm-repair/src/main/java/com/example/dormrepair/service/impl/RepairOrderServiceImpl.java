package com.example.dormrepair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.common.exception.BusinessException;
import com.example.dormrepair.common.utils.OrderNoUtil;
import com.example.dormrepair.entity.RepairOrder;
import com.example.dormrepair.entity.RepairRecord;
import com.example.dormrepair.mapper.RepairOrderMapper;
import com.example.dormrepair.mapper.RepairRecordMapper;
import com.example.dormrepair.service.OssStorageService;
import com.example.dormrepair.service.RepairOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairOrderServiceImpl implements RepairOrderService {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderServiceImpl.class);
    private static final int MAX_IMAGES = 3;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    @Autowired
    private RepairRecordMapper repairRecordMapper;

    @Autowired
    private OssStorageService ossStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepairOrder submitOrder(RepairOrder order, List<MultipartFile> images) {
        log.info("student submit order: studentId={}, repairTypeId={}",
                order.getStudentId(), order.getRepairTypeId());

        String orderNo = OrderNoUtil.generate();
        while (repairOrderMapper.selectByOrderNo(orderNo) != null) {
            orderNo = OrderNoUtil.generate();
        }

        order.setOrderNo(orderNo);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.insert(order);

        String imageUrls = buildImageUrls(images);
        if (imageUrls != null) {
            order.setImages(imageUrls);
            repairOrderMapper.updateById(order);
        }

        addRecord(order.getId(), order.getStudentId(), "student", "提交报修", null);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resubmitOrder(RepairOrder order, List<MultipartFile> images) {
        log.info("student resubmit order: id={}, studentId={}", order.getId(), order.getStudentId());

        RepairOrder existingOrder = repairOrderMapper.selectById(order.getId());
        if (existingOrder == null) {
            throw new BusinessException("工单不存在");
        }
        if (!existingOrder.getStudentId().equals(order.getStudentId())) {
            throw new BusinessException("只能修改自己的工单");
        }
        if (existingOrder.getStatus() != 1) {
            throw new BusinessException("只有被驳回的工单才能重新提交");
        }

        existingOrder.setTitle(order.getTitle());
        existingOrder.setRepairTypeId(order.getRepairTypeId());
        existingOrder.setBuildingId(order.getBuildingId());
        existingOrder.setRoomId(order.getRoomId());
        existingOrder.setDescription(order.getDescription());
        existingOrder.setStatus(0);
        existingOrder.setRepairUserId(null);
        existingOrder.setStartTime(null);
        existingOrder.setFinishTime(null);
        existingOrder.setEvaluateId(null);
        existingOrder.setUpdateTime(LocalDateTime.now());

        String imageUrls = buildImageUrls(images);
        if (imageUrls != null) {
            existingOrder.setImages(imageUrls);
        }

        repairOrderMapper.updateById(existingOrder);
        addRecord(existingOrder.getId(), existingOrder.getStudentId(), "student", "重新提交", null);
    }

    @Override
    public Page<RepairOrder> getPage(Page<RepairOrder> page, Long userId, String role,
                                     Long studentId, Integer status, List<Integer> statusList,
                                     Long repairTypeId, String keyword) {
        log.debug("query order page: role={}, status={}, statusList={}", role, status, statusList);

        if ("admin".equals(role)) {
            return repairOrderMapper.selectPageFilter(
                    page, studentId, null, status, statusList, repairTypeId, keyword);
        }
        if ("repair".equals(role)) {
            return repairOrderMapper.selectPageFilter(
                    page, null, userId, status, statusList, repairTypeId, keyword);
        }
        return repairOrderMapper.selectPageFilter(
                page, userId, null, status, statusList, repairTypeId, keyword);
    }

    @Override
    public Page<RepairOrder> getMyOrders(Page<RepairOrder> page, Long userId, String role) {
        if ("student".equals(role)) {
            return repairOrderMapper.selectPageFilter(page, userId, null, null, null, null, null);
        }
        if ("repair".equals(role)) {
            return repairOrderMapper.selectPageFilter(page, null, userId, null, null, null, null);
        }
        return new Page<>(1, 0);
    }

    @Override
    public RepairOrder getDetail(Long id, Long userId, String role) {
        RepairOrder order = repairOrderMapper.selectDetailById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if ("student".equals(role) && !order.getStudentId().equals(userId)) {
            throw new BusinessException("无权限查看该工单");
        }
        if ("repair".equals(role)
                && (order.getRepairUserId() == null || !order.getRepairUserId().equals(userId))) {
            throw new BusinessException("无权限查看该工单");
        }
        order.setImages(resolveDisplayImages(order.getImages()));
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id, Long studentId) {
        log.info("student cancel order: id={}, studentId={}", id, studentId);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!order.getStudentId().equals(studentId)) {
            throw new BusinessException("只能取消自己的工单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待审核的工单");
        }

        order.setStatus(7);
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.updateById(order);
        addRecord(id, studentId, "student", "取消", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long id, boolean approved, String auditRemark, Long adminId) {
        log.info("admin audit order: id={}, approved={}", id, approved);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能审核待审核的工单");
        }
        if (!approved && (auditRemark == null || auditRemark.isEmpty())) {
            throw new BusinessException("驳回时必须填写审核备注");
        }

        order.setStatus(approved ? 2 : 1);
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.updateById(order);
        addRecord(id, adminId, "admin", approved ? "审核通过" : "审核驳回", auditRemark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignOrder(Long id, Long repairUserId, Long adminId) {
        log.info("admin assign order: id={}, repairUserId={}", id, repairUserId);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只能给待派单状态的工单派单");
        }

        order.setRepairUserId(repairUserId);
        order.setStatus(3);
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.updateById(order);
        addRecord(id, adminId, "admin", "派单", "分配给维修人员ID:" + repairUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startRepair(Long id, Long repairUserId) {
        log.info("repair user start order: id={}, repairUserId={}", id, repairUserId);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!repairUserId.equals(order.getRepairUserId())) {
            throw new BusinessException("只能操作分配给自己的工单");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只能开始待维修状态的工单");
        }

        order.setStatus(4);
        order.setStartTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.updateById(order);
        addRecord(id, repairUserId, "repair", "开始维修", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishRepair(Long id, Long repairUserId, String finishRemark) {
        log.info("repair user finish order: id={}, repairUserId={}", id, repairUserId);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (!repairUserId.equals(order.getRepairUserId())) {
            throw new BusinessException("只能操作分配给自己的工单");
        }
        if (order.getStatus() != 4) {
            throw new BusinessException("只能完成维修中的工单");
        }

        order.setStatus(5);
        order.setFinishTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        repairOrderMapper.updateById(order);
        addRecord(id, repairUserId, "repair", "完成维修", finishRemark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id, Long adminId) {
        log.info("admin delete order: id={}", id);
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }

        addRecord(id, adminId, "admin", "删除工单", null);
        repairOrderMapper.deleteById(id);
    }

    private void addRecord(Long orderId, Long operatorId, String operatorRole,
                           String operation, String remark) {
        RepairRecord record = new RepairRecord();
        record.setOrderId(orderId);
        record.setOperation(operation);
        record.setOperatorId(operatorId);
        record.setOperatorRole(operatorRole);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        repairRecordMapper.insert(record);
    }

    private String buildImageUrls(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (MultipartFile file : images) {
            if (count >= MAX_IMAGES) {
                break;
            }
            if (file == null || file.isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(saveImageFile(file));
            count++;
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private String saveImageFile(MultipartFile file) {
        try {
            return ossStorageService.uploadImage(file);
        } catch (Exception e) {
            log.error("save image failed", e);
            throw new BusinessException("图片上传失败");
        }
    }

    private String resolveDisplayImages(String images) {
        if (images == null || images.isBlank()) {
            return images;
        }
        return Arrays.stream(images.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(ossStorageService::resolveImageUrl)
                .collect(Collectors.joining(","));
    }
}
