package com.pivot.aham.admin.service.mapper.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录用户session
 */
@TableName("sys_session")
@Data
@Accessors
public class SysSession extends BaseModel {
    @TableField("session_id")
    private String sessionId;
    @TableField("user_name")
    private String userName;
    @TableField("ip")
    private String ip;
    @TableField("start_time")
    private Date startTime;


}
