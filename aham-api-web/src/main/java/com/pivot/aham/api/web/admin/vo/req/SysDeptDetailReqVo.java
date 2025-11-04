package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@ApiModel("SysDeptDetailReqVo-请求对象说明")
public class SysDeptDetailReqVo extends BaseVo {
    @NotNull(message = "部门id不能为空")
    @ApiModelProperty(value = "id", required = true)
    private Long id;




}