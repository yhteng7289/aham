package com.pivot.aham.api.web.admin.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.admin.server.dto.SysUserDTO;
import com.pivot.aham.admin.server.dto.SysUserRoleDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysRoleRemoteService;
import com.pivot.aham.admin.server.remoteservice.SysUserRemoteService;
import com.pivot.aham.api.web.admin.vo.req.*;
import com.pivot.aham.api.web.admin.vo.res.SysUserResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.in.UserStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户管理控制器
 *
 * @author addison
 * @version 2018年11月18日 下午3:12:12
 */
@RestController
@Api(value = "用户管理", description = "用户管理")
@RequestMapping(value = "/api/v1/in/shiro")
public class AdminSysUserController extends AbstractController {
    @Resource
    private SysUserRemoteService sysUserService;
    @Resource
    private SysAuthorizeRemoteService sysAuthorizeService;
    @Resource
    private SysRoleRemoteService sysRoleService;

    @PostMapping("/user/save")
    @ApiOperation(value = "创建用户")
    @RequiresPermissions("sys:base:user:updateOrInsert")
    public Message save(@RequestBody SysUserSaveReqVo param) {
        //根据userName查找
        SysUserDTO sysUserQuery = new SysUserDTO();
        sysUserQuery.setUserName(param.getUserName());
        SysUserDTO sysUser1 = sysUserService.selectOne(sysUserQuery);
        if(sysUser1 != null){
            return Message.error(Resources.getMessage("USERNAME_REPEAT"));
        }

        SysUserDTO sysUserDTO = BeanMapperUtils.map(param,SysUserDTO.class);
        if(!StringUtils.isEmpty(param.getPassword())){
            sysUserDTO.setPassword(param.getPassword());
        }
        SysUserDTO sysUserDTORes = sysUserService.updateOrInsert(sysUserDTO);

        List<Long> roleIdList = param.getRoleIdList();
        if(roleIdList != null) {
            List<SysUserRoleDTO> listDto = Lists.newArrayList();
            for (Long roleId : roleIdList) {
                SysUserRoleDTO sysUserRoleDTO = new SysUserRoleDTO();
                sysUserRoleDTO.setRoleId(roleId);
                sysUserRoleDTO.setUserId(sysUserDTORes.getId());
                listDto.add(sysUserRoleDTO);
            }
            sysAuthorizeService.updateUserRole(listDto);
        }
        return Message.success();
    }

    @PostMapping("/user/update")
    @ApiOperation(value = "修改用户")
    @RequiresPermissions("sys:base:user:updateOrInsert")
       public Message update(@RequestBody SysUserUpdateReqVo param) {
//        AssertUtil.isNotBlank(param.getUserName(), "userName");
//        AssertUtil.length(param.getUserName(), 3, 15, "userName");
        if(param.getAction()==null){
            SysUserDTO sysUserDTO = BeanMapperUtils.map(param, SysUserDTO.class);
            sysUserDTO.setId(param.getUserId());
            if (!StringUtils.isEmpty(param.getPassword())) {
                sysUserDTO.setPassword(DigestUtil.md5Hex(param.getPassword()));
            }
            SysUserDTO sysUserDTORes = sysUserService.updateOrInsert(sysUserDTO);

            List<Long> roleIdList = param.getRoleIdList();
            if (roleIdList != null) {
                List<SysUserRoleDTO> listDto = Lists.newArrayList();
                for (Long roleId : roleIdList) {
                    SysUserRoleDTO sysUserRoleDTO = new SysUserRoleDTO();
                    sysUserRoleDTO.setRoleId(roleId);
                    sysUserRoleDTO.setUserId(sysUserDTORes.getId());
                    listDto.add(sysUserRoleDTO);
                }
            sysAuthorizeService.updateUserRole(listDto);
            }
        }else{
            if(param.getAction().equalsIgnoreCase("D")){
                SysUserDTO sysUserDTO = BeanMapperUtils.map(param, SysUserDTO.class);
                sysUserDTO.setId(param.getUserId());
                sysUserDTO.setUserStatus(UserStatusEnum.LOCK);
                sysUserService.updateOrInsert(sysUserDTO);
            }else{
                SysUserDTO sysUserDTO = BeanMapperUtils.map(param, SysUserDTO.class);
                sysUserDTO.setId(param.getUserId());
                String encrptyPass = DigestUtil.md5Hex("123456");
                sysUserDTO.setPassword("123456");
                sysUserService.updateOrInsert(sysUserDTO);
            }
        }
        return Message.success();
    }
    
    
 /*   public Message update(@RequestBody SysUserUpdateReqVo param) {
//        AssertUtil.isNotBlank(param.getUserName(), "userName");
//        AssertUtil.length(param.getUserName(), 3, 15, "userName");
        SysUserDTO sysUserDTO = BeanMapperUtils.map(param,SysUserDTO.class);
        sysUserDTO.setId(param.getUserId());
        if(!StringUtils.isEmpty(param.getPassword())){
            sysUserDTO.setPassword(DigestUtil.md5Hex(param.getPassword()));
        }
        SysUserDTO sysUserDTORes = sysUserService.updateOrInsert(sysUserDTO);

        List<Long> roleIdList = param.getRoleIdList();
        if(roleIdList != null) {
            List<SysUserRoleDTO> listDto = Lists.newArrayList();
            for (Long roleId : roleIdList) {
                SysUserRoleDTO sysUserRoleDTO = new SysUserRoleDTO();
                sysUserRoleDTO.setRoleId(roleId);
                sysUserRoleDTO.setUserId(sysUserDTORes.getId());
                listDto.add(sysUserRoleDTO);
            }
            sysAuthorizeService.updateUserRole(listDto);
        }
        return Message.success();
    }
*/

    @ApiOperation(value = "查询用户列表(分页)")
    @RequiresPermissions("sys:base:user:read")
    @PostMapping(value = "/user/list")
    public Message<Page<SysUserResVo>> queryPageList(@RequestBody SysUserPageReqVo sysUserPageReqVo) {
        //构造分页对象
        Page<SysUserDTO> rowBounds = new Page<>(
        sysUserPageReqVo.getPageNo(),sysUserPageReqVo.getPageSize());

        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanMapperUtils.copy(sysUserPageReqVo,sysUserDTO);
        sysUserDTO.setUserStatus(UserStatusEnum.NORMAL);
        Page<SysUserDTO> pagination = sysUserService.queryPageList(sysUserDTO,rowBounds);

        Page<SysUserResVo> sysUserPagination = new Page<>();
        Page<SysUserResVo> paginationRes = BeanMapperUtils.map(pagination,sysUserPagination.getClass());
//        List<SysUserResVo> lastList = BeanMapperUtils.mapList(paginationRes.getRecords(),SysUserResVo.class);
        List<SysUserResVo> lastList = Lists.newArrayList();
        List<SysUserDTO> sysUserDTOList = pagination.getRecords();
        for(SysUserDTO sysUser:sysUserDTOList){
            SysUserResVo sysUserResVo = new SysUserResVo();
            sysUserResVo.setUserId(sysUser.getId());
            sysUserResVo.setEmail(sysUser.getEmail());
            sysUserResVo.setMobile(sysUser.getMobile());
            sysUserResVo.setPassword(sysUser.getPassword());
            sysUserResVo.setRoleIdList(null);
            sysUserResVo.setRealName(sysUser.getRealName());
            sysUserResVo.setUserStatus(sysUser.getUserStatus());
            sysUserResVo.setUserName(sysUser.getUserName());
            lastList.add(sysUserResVo);
        }
        paginationRes.setRecords(lastList);
       return Message.success(paginationRes);

    }

    // 用户详细信息
    @ApiOperation(value = "用户详细信息")
    @RequiresPermissions("sys:base:user:read")
    @PostMapping(value = "/user/info")
    public Message<SysUserResVo> userInfo(@RequestBody SysUserDetailReqVo param) {
        SysUserDTO result = sysUserService.queryById(param.getUserId());
        result.setPassword(null);
        SysUserResVo sysUserResVo = BeanMapperUtils.map(result,SysUserResVo.class);
        sysUserResVo.setUserId(result.getId());


        List<SysUserRoleDTO> roles = sysAuthorizeService.getRolesByUserId(param.getUserId());
        List<Long> roleIdList = Lists.newArrayList();
        for(SysUserRoleDTO sysUserRoleDTO:roles){
            roleIdList.add(sysUserRoleDTO.getRoleId());
        }
        sysUserResVo.setRoleIdList(roleIdList);
        return Message.success(sysUserResVo);
    }


    // 用户详细信息
    @ApiOperation(value = "用户详细信息")
    @RequiresPermissions("sys:base:user:read")
    @PostMapping(value = "/user/info/forEdit")
    public Message<SysUserResVo> userInfoForEdit(@RequestBody SysUserDetailReqVo param) {
        SysUserDTO result = sysUserService.queryById(param.getUserId());
        result.setPassword(null);
        SysUserResVo sysUserResVo = BeanMapperUtils.map(result,SysUserResVo.class);
        sysUserResVo.setUserId(result.getId());

        List<SysUserRoleDTO> roles = sysAuthorizeService.getRolesByUserId(param.getUserId());
        List<Long> roleIdList = Lists.newArrayList();
        for(SysUserRoleDTO sysUserRoleDTO:roles){
            roleIdList.add(sysUserRoleDTO.getRoleId());
        }
        sysUserResVo.setRoleIdList(roleIdList);


        return Message.success(sysUserResVo);
    }

//    @ApiOperation(value = "删除用户")
//    @RequiresPermissions("sys:base:user:delete")
//    @DeleteMapping
//    public Message delete(@RequestBody SysUserDetailReqVo param) {
//        Assert.notNull(param.getUserId(), "UserID不能为空");
//        sysUserService.delete(param.getUserId());
//        return Message.success();
//    }

//    @ApiOperation(value = "当前用户权限信息")
//    @GetMapping(value = "/read/promission")
//    public Message<CurrentUserInfoResVo> promission() {
//        CurrentUserInfoResVo currentUserInfoResVo = new CurrentUserInfoResVo();
//        Long id = getCurrUser().getId();
//        SysUserDTO sysUser = sysUserService.queryById(id);
//        sysUser.setPassword(null);
//
//        SysUserResVo sysUserResVo = BeanMapperUtils.map(sysUser,SysUserResVo.class);
//
//        currentUserInfoResVo.setUser(sysUserResVo);
//        List<String> menus = sysAuthorizeService.queryPermissionByUserId(id);
//        currentUserInfoResVo.setMenus(menus);
//        return Message.success(currentUserInfoResVo);
//    }

    @ApiOperation(value = "当前用户信息")
    @PostMapping(value = "/info")
//    @RequiresPermissions("sys:base:user:read")
    public Message<SysUserResVo> current() {
        SysUserDTO result = sysUserService.queryById(getCurrUser().getId());
        result.setPassword(null);
        SysUserResVo sysUserResVo = BeanMapperUtils.map(result,SysUserResVo.class);
        sysUserResVo.setUserId(result.getId());


        List<String> permissionIds =  sysAuthorizeService.queryPermissionByUserId(getCurrUser().getId());
        sysUserResVo.setPermissionList(permissionIds);


        return Message.success(sysUserResVo);
    }

    @ApiOperation(value = "修改密码")
    @PostMapping(value = "/user/changePwd")
    @RequiresPermissions("sys:base:user:update")
    public Message updatePassword(@RequestBody SysUserChangePassowrdReqVo param) {
        String encryptPassword = DigestUtil.md5Hex(param.getOldPassword());
        SysUserDTO sysUser = sysUserService.queryById(getCurrUser().getId());
        if (!sysUser.getPassword().equals(encryptPassword)) {
            return Message.error(Resources.getMessage("ORIGINAL_PASSWORD"));
        }
//        sysUser.setPassword(param.getPassword());

        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setPassword(param.getPassword());
        sysUserDTO.setId(sysUser.getId());
        sysUserService.updateOrInsert(sysUserDTO);

        return Message.success();
    }

    @ApiOperation(value = "重置密码")
    @PostMapping(value = "/user/resetPwd")
    @RequiresPermissions("sys:base:user:update")
    public Message resetPassword(@RequestBody SysUserResetPassowrdReqVo param) {
        String encryptPassword = "123456";

        SysUserDTO sysUserDTO = new SysUserDTO();
        sysUserDTO.setUserName(param.getUserName());
        SysUserDTO sysUser = sysUserService.selectOne(sysUserDTO);
        sysUser.setPassword(encryptPassword);
        sysUserService.updateOrInsert(sysUser);

        return Message.success();
    }

    public static void main(String[] args){
        System.out.println(DigestUtil.md5Hex("123456"));
    }
}
