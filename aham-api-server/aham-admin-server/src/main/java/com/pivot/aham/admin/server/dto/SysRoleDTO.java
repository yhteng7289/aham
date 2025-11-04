package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.in.RoleStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色
 */
@Data
@Accessors(chain = true)
public class SysRoleDTO extends BaseDTO {
    private String roleName;
    private Long deptId;
    private String deptName;
    private String description;
    private RoleStatusEnum roleStatus;

    //Added By WooiTatt
    private String deleted;


}