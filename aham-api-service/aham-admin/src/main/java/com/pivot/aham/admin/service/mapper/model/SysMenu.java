package com.pivot.aham.admin.service.mapper.model;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.in.MenuTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 权限
 */
@TableName("sys_menu")
@Data
@Accessors(chain = true)
public class SysMenu extends BaseModel {
    /**
     * 权限名称
     */
    @TableField("permission_Name")
    private String permissionName;
    /**
     * 父id
     */
    @TableField("parent_id")
    private Long parentId;
    /**
     * 权限
     */
    @TableField("permission_code")
    private String permissionCode;
    /**
     * 权限类型
     */
    @TableField("node_type")
    private MenuTypeEnum nodeType;

    /**
     * 父id
     */
    @TableField(exist = false)
    private String parentName;

    @TableField(value = "description")
    private String description;




}