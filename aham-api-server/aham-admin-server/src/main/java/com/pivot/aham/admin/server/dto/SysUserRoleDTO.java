package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户-角色关系
 */
@Data
@Accessors(chain=true)
public class SysUserRoleDTO extends BaseDTO {
    private Long userId;
    private Long roleId;
    private String roleName;
}
