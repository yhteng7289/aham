package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("SysRolePageVo-请求对象说明")
public class SysRolePageVo extends BaseVo {
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
}