package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.in.RoleStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain=true)
@ApiModel("SysUserRoleResVo-请求对象说明")
public class SysUserRoleResVo extends BaseModel {
    @ApiModelProperty(value = "角色id", required = true)
    private String roleId;
    @ApiModelProperty(value = "角色名", required = true)
    private String roleName;
    @ApiModelProperty(value = "部门id", required = true)
    private Long deptId;
    //    @ApiModelProperty(value = "角色类型", required = true)
//    private RoleTypeEnum roleType;
//    @ApiModelProperty(value = "部门名称", required = true)
    private String deptName;
    @ApiModelProperty(name = "权限ID清单", required = false)
    private List<Long> permissionIdList;

    @ApiModelProperty(value = "备注", required = true)
    private String description;
    @ApiModelProperty(value = "角色状态", required = true)
    private RoleStatusEnum roleStatus;
}
