package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("SysUserSaveReqVo-请求对象说明")
public class SysUserChangePassowrdReqVo extends BaseVo {
	@ApiModelProperty(value = "密码",required = false)
	private String password;
	@ApiModelProperty(value = "旧密码",required = false)
	private String oldPassword;
//	@NotNull(message = "用户userId不能为空")
//	@ApiModelProperty(value = "userId",required = true)
//	private Long userId;


}