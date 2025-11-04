package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.enums.in.UserStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("SysUserResVo-请求对象说明")
public class SysUserResVo implements Serializable {
	@ApiModelProperty(value = "id",required = true)
	private Long userId;
	@ApiModelProperty(value = "登录名",required = true)
	private String userName;
	@ApiModelProperty(value = "电子邮箱",required = true)
	private String email;
	@ApiModelProperty(value = "手机号",required = true)
	private String mobile;
	@ApiModelProperty(name = "用户状态", required = true)
	private UserStatusEnum userStatus;
	@ApiModelProperty(name = "姓名", required = true)
	private String realName;
	@ApiModelProperty(name = "权限列表", required = false)
	private List<String> permissionList;
	@ApiModelProperty(name = "角色列表", required = false)
	private List<Long> roleIdList;
	@ApiModelProperty(name = "密码", required = true)
	private String password;





}