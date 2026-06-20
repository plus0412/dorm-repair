package com.example.dormrepair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.dormrepair.entity.RepairOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 报修工单业务接口
 */
public interface RepairOrderService {

    /**
     * 学生提交报修工单
     * @param order 工单基本信息（studentId、buildingId、roomId、repairTypeId、description）
     * @param images 报修图片（可选，最多3张）
     * @return 保存后的工单（含 orderNo）
     */
    RepairOrder submitOrder(RepairOrder order, List<MultipartFile> images);

    /**
     * 学生修改被驳回的工单后重新提交
     */
    void resubmitOrder(RepairOrder order, List<MultipartFile> images);

    /**
     * 分页查询工单（角色权限过滤）
     * 学生：只看自己的；维修人员：只看分配给自己的；管理员：看全部
     */
    Page<RepairOrder> getPage(Page<RepairOrder> page, Long userId, String role,
                               Long studentId, Integer status, List<Integer> statusList,
                               Long repairTypeId, String keyword);

    /**
     * 查询我的工单（学生/维修人员）
     */
    Page<RepairOrder> getMyOrders(Page<RepairOrder> page, Long userId, String role);

    /**
     * 查询工单详情（含权限校验）
     */
    RepairOrder getDetail(Long id, Long userId, String role);

    /**
     * 学生取消工单（状态 0 → 7）
     */
    void cancelOrder(Long id, Long studentId);

    /**
     * 管理员审核工单（状态 0 → 2 通过，0 → 1 驳回）
     * @param id 工单ID
     * @param approved true=通过，false=驳回
     * @param auditRemark 审核意见（驳回时必填）
     * @param adminId 操作人ID
     */
    void auditOrder(Long id, boolean approved, String auditRemark, Long adminId);

    /**
     * 管理员派单（状态 2 → 3）
     * @param id 工单ID
     * @param repairUserId 维修人员ID
     * @param adminId 操作人ID
     */
    void assignOrder(Long id, Long repairUserId, Long adminId);

    /**
     * 维修人员开始维修（状态 3 → 4）
     */
    void startRepair(Long id, Long repairUserId);

    /**
     * 维修人员完成维修（状态 4 → 5）
     * @param id 工单ID
     * @param repairUserId 维修人员ID
     * @param finishRemark 完成备注
     */
    void finishRepair(Long id, Long repairUserId, String finishRemark);

    /**
     * 管理员删除工单
     */
    void deleteOrder(Long id, Long adminId);
}
