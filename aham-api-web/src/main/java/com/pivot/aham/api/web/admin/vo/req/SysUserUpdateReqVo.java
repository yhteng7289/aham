package com.pivot.aham.api.web.admin.vo.req;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.in.UserStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("SysUserSaveReqVo-请求对象说明")
public class SysUserUpdateReqVo extends BaseVo {
	@ApiModelProperty(value = "登录名",required = false)
	private String userName;
	@ApiModelProperty(value = "密码",required = false)
	private String password;
	@ApiModelProperty(value = "邮箱",required = false)
	private String email;
	@ApiModelProperty(name = "用户状态", required = true)
	private UserStatusEnum userStatus;
	@ApiModelProperty(value = "姓名",required = false)
	private String realName;
	@ApiModelProperty(value = "手机号",required = false)
	private String mobile;
	@ApiModelProperty(value = "所属部门ID",required = false)
	private Long deptId;
	@NotNull(message = "用户userId不能为空")
	@ApiModelProperty(value = "userId",required = true)
	private Long userId;
	@ApiModelProperty(name = "角色列表", required = false)
	private List<Long> roleIdList;
        
        private String action;

	public static void main(String[] args){
		SysUserUpdateReqVo sysUserSaveReqVo = new SysUserUpdateReqVo();
		sysUserSaveReqVo.setUserName("test");
		sysUserSaveReqVo.setPassword("123456");
		sysUserSaveReqVo.setUserStatus(UserStatusEnum.NORMAL);
		sysUserSaveReqVo.setRealName("testReal");
		sysUserSaveReqVo.setMobile("19872673684");
		sysUserSaveReqVo.setEmail("addison@163.com");
		sysUserSaveReqVo.setDeptId(1L);
		sysUserSaveReqVo.setUserId(2L);
		List<Long> longs = Lists.newArrayList();
		longs.add(1L);
		sysUserSaveReqVo.setRoleIdList(longs);

		System.out.println(JSON.toJSONString(sysUserSaveReqVo));


	}

}