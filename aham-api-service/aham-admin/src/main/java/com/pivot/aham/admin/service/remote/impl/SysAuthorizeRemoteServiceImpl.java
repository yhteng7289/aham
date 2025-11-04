package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.dto.SysRoleMenuDTO;
import com.pivot.aham.admin.server.dto.SysUserRoleDTO;
import com.pivot.aham.admin.server.remoteservice.SysAuthorizeRemoteService;
import com.pivot.aham.admin.service.mapper.model.SysMenu;
import com.pivot.aham.admin.service.mapper.model.SysRole;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import com.pivot.aham.admin.service.mapper.model.SysUserRole;
import com.pivot.aham.admin.service.service.SysAuthorizeService;
import com.pivot.aham.admin.service.service.SysRoleService;
import com.pivot.aham.common.core.util.BeanMapperUtils;

import javax.annotation.Resource;
import java.util.List;


@Service(interfaceClass = SysAuthorizeRemoteService.class)
public class SysAuthorizeRemoteServiceImpl implements SysAuthorizeRemoteService {

    @Resource
    private SysAuthorizeService sysAuthorizeService;
    @Resource
    private SysRoleService sysRoleService;

    @Override
    public List<Long> queryMenuIdsByUserId(Long userId) {
        return sysAuthorizeService.queryMenuIdsByUserId(userId);
    }

    @Override
    public List<SysUserRoleDTO> getRolesByUserId(Long userId) {
        List<SysUserRole> sysUserRoles = sysAuthorizeService.getRolesByUserId(userId);
        List<SysUserRoleDTO> res = BeanMapperUtils.mapList(sysUserRoles,SysUserRoleDTO.class);
        for(SysUserRoleDTO role:res){
            SysRole sysRole = sysRoleService.queryById(role.getRoleId());
            role.setRoleName(sysRole.getRoleName());
            role.setRoleId(sysRole.getId());
        }

        return res;
    }

    @Override
    public void updateUserRole(List<SysUserRoleDTO> sysUserRoles) {
        List<SysUserRole> sysUserRoleList = BeanMapperUtils.mapList(sysUserRoles,SysUserRole.class);
        sysAuthorizeService.updateUserRole(sysUserRoleList);
    }

    @Override
    public List<Long> queryMenuIdsByRoleId(Long roleId) {
        return sysAuthorizeService.queryMenuIdsByRoleId(roleId);
    }

    @Override
    public List<SysMenuDTO> queryMenusByRoleId(Long roleId) {
        List<SysMenu> sysMenus = sysAuthorizeService.queryMenusByRoleId(roleId);
        List<SysMenuDTO> sysMenuDTOS = BeanMapperUtils.mapList(sysMenus,SysMenuDTO.class);

        return sysMenuDTOS;
    }

    @Override
    public void updateRoleMenu(List<SysRoleMenuDTO> sysRoleMenus) {
        List<SysRoleMenu> sysRoleMenuList = BeanMapperUtils.mapList(sysRoleMenus, SysRoleMenu.class);
        sysAuthorizeService.updateRoleMenu(sysRoleMenuList);
    }

    @Override
    public List<String> queryPermissionByUserId(Long userId) {
        return sysAuthorizeService.queryPermissionByUserId(userId);
    }

    @Override
    public List<String> queryPermissionByRoleId(Long roleId) {
        return sysAuthorizeService.queryPermissionByRoleId(roleId);
    }
}
