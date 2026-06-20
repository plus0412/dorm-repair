package com.example.dormrepair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 报修工单实体类，对应 repair_order 表
 */
@TableName("repair_order")
public class RepairOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;          // 报修标题
    private String orderNo;         // 工单编号 BX + yyyyMMddHHmmss + 3位随机数
    private Long studentId;       // 报修学生ID
    @TableField(value = "student_name", exist = false)
    private String studentName;   // 关联查询填充，非表字段
    @TableField("building_id")
    private Long buildingId;      // 宿舍楼ID
    @TableField("room_id")
    private Long roomId;          // 宿舍房间ID
    @TableField("repair_type_id")
    private Long repairTypeId;    // 报修类型ID
    private String description;   // 报修描述
    private Integer status;       // 0待审核 1审核驳回 2待派单 3待维修 4维修中 5已完成 6已评价 7已取消
    @TableField("repair_user_id")
    private Long repairUserId;    // 分配的维修人员ID
    @TableField(exist = false)
    private LocalDateTime assignTime;    // 派单时间
    @TableField("start_time")
    private LocalDateTime startTime;     // 开始维修时间
    @TableField("finish_time")
    private LocalDateTime finishTime;    // 完成时间
    @TableField("image_urls")
    private String images;         // 图片路径，逗号分隔
    @TableField("evaluate_id")
    private Long evaluateId;       // 评价记录ID
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    // 关联查询填充字段（非表字段）
    @TableField(exist = false)
    private String buildingName;   // 宿舍楼名称
    @TableField(exist = false)
    private String roomNo;         // 房间号
    @TableField(exist = false)
    private String repairTypeName; // 报修类型名称
    @TableField(exist = false)
    private String repairUserName; // 维修人员姓名

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getRepairTypeId() { return repairTypeId; }
    public void setRepairTypeId(Long repairTypeId) { this.repairTypeId = repairTypeId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getRepairUserId() { return repairUserId; }
    public void setRepairUserId(Long repairUserId) { this.repairUserId = repairUserId; }

    public LocalDateTime getAssignTime() { return assignTime; }
    public void setAssignTime(LocalDateTime assignTime) { this.assignTime = assignTime; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public Long getEvaluateId() { return evaluateId; }
    public void setEvaluateId(Long evaluateId) { this.evaluateId = evaluateId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getRepairTypeName() { return repairTypeName; }
    public void setRepairTypeName(String repairTypeName) { this.repairTypeName = repairTypeName; }

    public String getRepairUserName() { return repairUserName; }
    public void setRepairUserName(String repairUserName) { this.repairUserName = repairUserName; }
}
