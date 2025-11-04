package com.pivot.aham.api.web.admin.controller;

import com.google.common.collect.Lists;
import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.dto.SysUserRoleDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.api.web.admin.support.TreeBuilder;
import com.pivot.aham.api.web.admin.vo.req.SysRoleMenuVo;
import com.pivot.aham.api.web.admin.vo.req.SysUserRoleReqVo;
import com.pivot.aham.api.web.admin.vo.res.SysMenuResVo;
import com.pivot.aham.api.web.admin.vo.res.SysUserRoleResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限查询
 *
 * @author addison
 * @since 2018年11月19日
 */
@RestController
@RequestMapping(value = "/api/v1/in/shiro")
@Api(value = "权限管理", description = "权限管理")
public class AdminSysAuthorizeController extends AbstractController {
    @Resource
    private SysAuthorizeRemoteService sysAuthorizeService;

    @ApiOperation(value = "获取用户角色列表")
    @PostMapping(value = "/role/list")
    @RequiresPermissions("sys:base:permission:read")
    public Message<List<SysUserRoleResVo>> getUserRole(@RequestBody SysUserRoleReqVo param) {
        List<SysUserRoleDTO> roles = sysAuthorizeService.getRolesByUserId(param.getUserId());
        List<SysUserRoleResVo> roleList =  BeanMapperUtils.mapList(roles,SysUserRoleResVo.class);

        for(SysUserRoleResVo sysRoleResVo:roleList){
            List<Long> permissionIds = sysAuthorizeService.queryMenuIdsByRoleId(Long.valueOf(sysRoleResVo.getRoleId()));
            sysRoleResVo.setPermissionIdList(permissionIds);
        }
        return Message.success(roleList);
    }


//    @ApiOperation(value = "修改用户角色")
//    @PostMapping(value = "/user/update/role")
//    @RequiresPermissions("sys:base:permission:updateOrInsert")
//    public Message userRole(@RequestBody List<SysUserRoleReqVo> list) {
//        Long userId = null;
//        for (SysUserRoleReqVo sysUserRole : list) {
//            if (sysUserRole.getUserId() != null) {
//                if (userId != null && sysUserRole.getUserId() != null
//                        && !userId.equals( sysUserRole.getUserId())) {
//                    throw new IllegalParameterException("userid参数错误.");
//                }
//                userId = sysUserRole.getUserId();
//            }
//            sysUserRole.setCreateTime(new Date());
//            sysUserRole.setUpdateTime(new Date());
//        }
//        List<SysUserRoleDTO> listDto = BeanMapperUtils.mapList(list,SysUserRoleDTO.class);
//        sysAuthorizeService.updateUserRole(listDto);
//        return Message.success();
//    }

//    @ApiOperation(value = "获取角色菜单编号")
//    @PostMapping(value = "role/read/menu")
//    @RequiresPermissions("sys:base:permission:read")
//    public Message<List<Long>> getRoleMenu(@RequestBody SysRoleMenuReqVo param) {
//        List<Long> menus = sysAuthorizeService.queryMenuIdsByRoleId(param.getRoleId());
//        return Message.success(menus);
//    }

//    @ApiOperation(value = "修改角色菜单")
//    @PostMapping(value = "/role/update/menu")
//    @RequiresPermissions("sys:base:permission:updateOrInsert")
//    public Message roleMenu(@RequestBody List<SysRoleMenuReqVo> list) {
//        Long roleId = null;
//        for (SysRoleMenuReqVo sysRoleMenu : list) {
//            if (sysRoleMenu.getRoleId() != null) {
//                if (roleId != null && sysRoleMenu.getRoleId() != null
//                        && roleId.longValue() != sysRoleMenu.getRoleId()) {
//                    throw new IllegalParameterException("参数错误.");
//                }
//                roleId = sysRoleMenu.getRoleId();
//            }
//            sysRoleMenu.setCreateTime(new Date());
//            sysRoleMenu.setUpdateTime(new Date());
//        }
//        List<SysRoleMenuDTO> listDto = BeanMapperUtils.mapList(list,SysRoleMenuDTO.class);
//        sysAuthorizeService.updateRoleMenu(listDto);
//
//        return Message.success();
//    }

    @ApiOperation(value = "根据角色id查询菜单树")
    @PostMapping(value = "/permission/tree/role")
    @RequiresPermissions("sys:base:permission:read")
    public Message<List<SysMenuResVo>> getMenuTree(@RequestBody SysRoleMenuVo sysRoleMenuVo) {
        List<SysMenuDTO> list = sysAuthorizeService.queryMenusByRoleId(sysRoleMenuVo.getId());

        List<SysMenuResVo> resList = Lists.newArrayList();
        for(SysMenuDTO sysMenu:list){
            SysMenuResVo sysMenuResVo = new SysMenuResVo();
            sysMenuResVo.setPermissionId(sysMenu.getId());
            sysMenuResVo.setDescription(sysMenu.getDescription());
            sysMenuResVo.setNodeType(sysMenu.getNodeType());
            sysMenuResVo.setParentId(sysMenu.getParentId());
            sysMenuResVo.setPermissionCode(sysMenu.getPermissionCode());
            sysMenuResVo.setPermissionName(sysMenu.getPermissionName());
            resList.add(sysMenuResVo);
        }
        List<SysMenuResVo> listTree = TreeBuilder.buildTree(resList);
        return Message.success(listTree);
    }
}
