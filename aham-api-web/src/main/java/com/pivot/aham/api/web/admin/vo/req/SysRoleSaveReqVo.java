package com.pivot.aham.api.web.admin.vo.req;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.in.RoleStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("SysRoleUpdateReqVo-请求对象说明")
public class SysRoleSaveReqVo extends BaseVo {
//    @ApiModelProperty(value = "角色id", required = true)
//    private String roleId;
    @ApiModelProperty(value = "角色名", required = true)
    private String roleName;
    @ApiModelProperty(value = "部门id", required = true)
    private Long deptId;
    //    @ApiModelProperty(value = "角色类型", required = true)
//    private RoleTypeEnum roleType;
//    @ApiModelProperty(value = "部门名称", required = true)
//    private String deptName;
    @ApiModelProperty(name = "权限ID清单", required = false)
    private List<Long> permissionIdList;

    @ApiModelProperty(value = "备注", required = true)
    private String description;
    @ApiModelProperty(value = "角色状态", required = true)
    private RoleStatusEnum roleStatus;


    public static void main(String[] args){
        SysRoleSaveReqVo sysRoleSaveReqVo = new SysRoleSaveReqVo();
        sysRoleSaveReqVo.setDeptId(1L);
        sysRoleSaveReqVo.setDescription("testtest");
        List<Long> testP = Lists.newArrayList(1L);
        sysRoleSaveReqVo.setPermissionIdList(testP);
        sysRoleSaveReqVo.setRoleName("testRole");
        sysRoleSaveReqVo.setRoleStatus(RoleStatusEnum.NORMAL);

        System.out.println(JSON.toJSONString(sysRoleSaveReqVo));

    }




}