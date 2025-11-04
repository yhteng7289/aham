package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.in.UserStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 系统用户
 */
@TableName(value = "sys_user", resultMap = "sysUserMap")
@Data
@Accessors(chain = true)
public class SystemUserPO extends BaseModel {
	/**
	 * 登陆帐户
	 */
	@TableField("user_name")
	private String userName;
	/**
	 * 密码
	 */
	@TableField("password")
	private String password;
	/**
	 * 姓名
	 */
	@TableField("real_name")
	private String realName;
	/**
	 * 联系方式
	 */
	@TableField("mobile")
	private String mobile;
	/**
	 * 所属部门
	 */
	@TableField("dept_id")
	private Long deptId;
	// 邮箱地址
	@TableField("email")
	private String email;
	// 用户状态(0:正常; 1:锁定)
	@TableField("user_status")
	private UserStatusEnum userStatus;


	@TableField(exist = false)
	private String oldPassword;
	@TableField(exist = false)
	private String deptName;
	@TableField(exist = false)
	private String userTypeText;
	@TableField(exist = false)
	private String permissionCode;







}