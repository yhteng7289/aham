package com.pivot.aham.admin.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户-角色关系
 */
@TableName("sys_user_role")
@Data
@Accessors(chain=true)
public class SysUserRole extends BaseModel {
    @TableField("user_id")
    private Long userId;
    @TableField("role_id")
    private Long roleId;
}
