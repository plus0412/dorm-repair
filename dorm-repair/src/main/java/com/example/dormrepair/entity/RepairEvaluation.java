package com.example.dormrepair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 维修评价实体类，对应 repair_evaluation 表
 */
@TableName("repair_evaluation")
public class RepairEvaluation {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;          // 工单ID
    @TableField("student_id")
    private Long studentId;        // 评价学生ID
    @TableField("repair_user_id")
    private Long repairUserId;    // 维修人员ID
    private Integer rating;        // 评分：1-5
    private String comment;        // 评价内容
    @TableField("create_time")
    private LocalDateTime createTime;

    // 关联字段（用于展示，不存入数据库）
    @TableField(exist = false)
    private String studentName;    // 学生姓名
    @TableField(exist = false)
    private String repairUserName; // 维修人员姓名

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getRepairUserId() { return repairUserId; }
    public void setRepairUserId(Long repairUserId) { this.repairUserId = repairUserId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRepairUserName() { return repairUserName; }
    public void setRepairUserName(String repairUserName) { this.repairUserName = repairUserName; }
}
