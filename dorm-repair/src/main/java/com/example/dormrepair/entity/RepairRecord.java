package com.example.dormrepair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 维修记录实体类，对应 repair_record 表
 */
@TableName("repair_record")
public class RepairRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;
    @TableField("operation")
    private String operation;     // 操作描述：提交报修/审核通过/审核驳回/派单/开始维修/完成维修/取消
    @TableField("operator_id")
    private Long operatorId;
    @TableField("operator_role")
    private String operatorRole;
    private String remark;        // 备注（审核意见、维修结果等）
    @TableField("create_time")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public String getOperatorRole() { return operatorRole; }
    public void setOperatorRole(String operatorRole) { this.operatorRole = operatorRole; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
