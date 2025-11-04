package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@ApiModel("SysMenuDetailReqVo-请求对象说明")
public class SysMenuDetailReqVo extends BaseVo {
    @NotNull(message = "权限id不能为空")
    @ApiModelProperty(value = "id", required = true)
    private Long permissionId;




}