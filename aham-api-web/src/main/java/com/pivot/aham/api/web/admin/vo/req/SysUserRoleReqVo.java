package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@ApiModel("SysUserRoleReqVo-请求对象说明")
public class SysUserRoleReqVo extends BaseModel {
    @ApiModelProperty(value = "用户id", required = false)
    private Long userId;
//    @ApiModelProperty(value = "角色id", required = false)
//    private Long roleId;
}
