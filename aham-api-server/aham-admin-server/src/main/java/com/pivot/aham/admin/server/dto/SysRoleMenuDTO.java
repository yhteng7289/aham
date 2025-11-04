package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色-权限关系
 */
@Data
@Accessors(chain = true)
public class SysRoleMenuDTO extends BaseDTO {
    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 权限id
     */
    private Long permissionId;
}
