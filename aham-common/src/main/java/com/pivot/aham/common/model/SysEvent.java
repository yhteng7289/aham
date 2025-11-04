package com.pivot.aham.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

@TableName("sys_event")
@Data
@Accessors(chain = true)
public class SysEvent extends BaseModel {
    @TableField("title")
    private String title;
    private String requestUri;
    @TableField("parameters")
    private String parameters;
    @TableField("method")
    private String method;
    private String clientHost;
    private String userAgent;
    @TableField("status")
    private Integer status;
    @TableField("user_name")
    private String userName;
    @TableField("user_phone")
    private String userPhone;
    private String responseStr;

}
