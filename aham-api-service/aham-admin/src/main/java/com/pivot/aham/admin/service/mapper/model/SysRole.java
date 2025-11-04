package com.pivot.aham.admin.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.in.RoleStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色
 */
@TableName("sys_role")
@Data
@Accessors(chain = true)
public class SysRole extends BaseModel {
    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;
    /**
     * 部门id
     */
    @TableField("dept_id")
    private Long deptId;
//    /**
//     * 角色类型
//     */
//    @TableField("role_type")
//    private RoleTypeEnum roleType;
    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;
    @TableField("description")
    private String description;
    @TableField("role_status")
    private RoleStatusEnum roleStatus;
    
    //Added By WooiTatt
    @TableField("deleted")
    private String deleted;


}