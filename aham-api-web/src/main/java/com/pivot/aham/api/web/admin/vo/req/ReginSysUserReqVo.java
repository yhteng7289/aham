package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.in.UserStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("ReginSysUserReqVo-请求对象说明")
public class ReginSysUserReqVo extends BaseVo {
	/**
	 * 登陆帐户
	 */
	@NotNull(message="登录名不能为空")
	@ApiModelProperty(value = "登录名", required = true)
	private String userName;
	/**
	 * 密码
	 */
	@NotNull(message="密码不能为空")
	@ApiModelProperty(value = "密码", required = true)
	private String password;
	/**
	 * 用户类型(1普通用户2管理员3系统用户)
	 */
	@ApiModelProperty(value = "用户类型(1普通用户2管理员3系统用户)", required = true)
	private UserStatusEnum userType;
	/**
	 * 电话
	 */
	@ApiModelProperty(value = "手机号", required = true)
	private String mobile;
	/**
	 * 部门编号
	 */
	@ApiModelProperty(value = "所属部门", required = true)
	private Long deptId;
	/**
	 * 姓名
	 */
	@ApiModelProperty(value = "姓名", required = true)
	private String realName;

	@ApiModelProperty(name = "角色列表", required = false)
	private List<Long> roleIdList;
}