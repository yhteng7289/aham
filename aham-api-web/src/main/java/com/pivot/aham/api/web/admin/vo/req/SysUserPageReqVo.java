package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("SysUserPageReqVo-请求对象说明")
public class SysUserPageReqVo extends BaseVo {
//	@ApiModelProperty(value = "登录名",required = false)
//	private String account;
//	@ApiModelProperty(value = "密码",required = false)
//	private String password;
//	@ApiModelProperty(value = "用户类型",required = false)
//	private UserStatusEnum userType;
//	@ApiModelProperty(value = "姓名",required = false)
//	private String userName;
//	@ApiModelProperty(value = "手机号",required = false)
//	private String phone;
	@ApiModelProperty(value = "所属部门ID",required = false)
	private Long deptId;
	@ApiModelProperty(value = "页码", required = true)
	private Integer pageNo;
	@ApiModelProperty(value = "页大小", required = true)
	private Integer pageSize;
//	@ApiModelProperty(value = "是否升序", required = true)
//	private Boolean isAsc;
//	@ApiModelProperty(value = "排序字符串", required = true)
//	private String orderBy;



}