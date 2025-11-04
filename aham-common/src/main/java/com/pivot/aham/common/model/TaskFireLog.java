package com.pivot.aham.common.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("task_fire_log")
public class TaskFireLog implements Serializable {
    private Long id;
    private String groupName;
    private String taskName;
    private Date startTime;
    private Date endTime;
    private String status;
    private String serverHost;
//    private String serverDuid;
    private String fireInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost == null ? null : serverHost.trim();
    }

//    public String getServerDuid() {
//        return serverDuid;
//    }

//    public void setServerDuid(String serverDuid) {
//        this.serverDuid = serverDuid == null ? null : serverDuid.trim();
//    }

    public String getFireInfo() {
        return fireInfo;
    }
    public void setFireInfo(String fireInfo) {
        this.fireInfo = fireInfo == null ? null : fireInfo.trim();
    }
}