package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.in.RoleTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysRoleListVo-请求对象说明")
public class SysRoleListVo extends BaseVo {
    @ApiModelProperty(value = "角色名", required = false)
    private String roleName;
    @ApiModelProperty(value = "部门id", required = false)
    private Long deptId;
    @ApiModelProperty(value = "角色类型", required = false)
    private RoleTypeEnum roleType;
    @ApiModelProperty(value = "部门名称", required = false)
    private String deptName;

}