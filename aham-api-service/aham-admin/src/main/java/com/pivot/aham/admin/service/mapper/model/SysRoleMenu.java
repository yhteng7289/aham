package com.pivot.aham.admin.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色-权限关系
 */
@TableName("sys_role_menu")
@Data
@Accessors(chain = true)
public class SysRoleMenu extends BaseModel {
    /**
     * 角色id
     */
    @TableField("role_id")
    private Long roleId;
    /**
     * 权限id
     */
    @TableField("permission_id")
    private Long permissionId;
}
