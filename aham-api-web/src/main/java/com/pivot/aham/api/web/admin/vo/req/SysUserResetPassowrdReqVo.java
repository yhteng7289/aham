package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("SysUserSaveReqVo-请求对象说明")
public class SysUserResetPassowrdReqVo extends BaseVo {
	@ApiModelProperty(value = "登录名",required = false)
	private String userName;


}