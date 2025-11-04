package com.pivot.aham.admin.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.in.UserStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 系统用户
 */
@Data
@Accessors(chain = true)
public class SysUserDTO extends BaseDTO {
	/**
	 * 登陆帐户
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 姓名
	 */
	private String realName;
	/**
	 * 联系方式
	 */
	private String mobile;
	/**
	 * 所属部门
	 */
	private Long deptId;
	// 邮箱地址
	private String email;
	// 用户状态(0:正常; 1:锁定)
	private UserStatusEnum userStatus;


	private String oldPassword;
	private String deptName;
	private String userTypeText;
	private String permission;


}