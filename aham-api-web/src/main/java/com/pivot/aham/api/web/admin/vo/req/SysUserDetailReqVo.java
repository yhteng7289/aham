package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("SysUserDetailReqVo-请求对象说明")
public class SysUserDetailReqVo extends BaseVo {
	@NotNull(message = "用户id不能为空")
	@ApiModelProperty(value = "id",required = true)
	private Long userId;

}