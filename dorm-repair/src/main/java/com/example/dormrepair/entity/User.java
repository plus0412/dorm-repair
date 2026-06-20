package com.example.dormrepair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户实体类，对应 sys_user 表
 */
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String role;       // admin / student / repair
    private String gender;     // 男 / 女
    @TableField("dorm_building_id")
    private Long dormBuildingId;
    @TableField("dorm_room_id")
    private Long dormRoomId;
    private Integer status;     // 1=正常 0=禁用
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Long getDormBuildingId() { return dormBuildingId; }
    public void setDormBuildingId(Long dormBuildingId) { this.dormBuildingId = dormBuildingId; }

    public Long getDormRoomId() { return dormRoomId; }
    public void setDormRoomId(Long dormRoomId) { this.dormRoomId = dormRoomId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
