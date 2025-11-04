package com.pivot.aham.admin.server.dto;


import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.in.MenuTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 权限
 */
@Data
@Accessors(chain = true)
public class SysMenuDTO extends BaseDTO {
    /**
     * 权限名称
     */
    private String permissionName;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 权限
     */
    private String permissionCode;
    /**
     * 权限类型
     */
    private MenuTypeEnum nodeType;

    /**
     * 父id
     */
    private String parentName;

    /**
     * 描述
     */
    private String description;


}