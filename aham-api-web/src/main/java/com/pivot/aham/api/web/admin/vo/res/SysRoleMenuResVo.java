package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.core.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysRoleMenuResVo-请求对象说明")
public class SysRoleMenuResVo extends BaseModel {
    @ApiModelProperty(value = "角色id",required = true)
    private Long roleId;
    @ApiModelProperty(value = "菜单id",required = true)
    private Long menuId;
}
