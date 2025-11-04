package com.pivot.aham.admin.server.remoteservice;


import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.dto.SysRoleMenuDTO;
import com.pivot.aham.admin.server.dto.SysUserRoleDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;

import java.util.List;

/**
 * 权限认证
 * @author addison
 * @since 2018年11月11日 上午10:59:37
 */
public interface SysAuthorizeRemoteService extends BaseRemoteService {
    List<SysUserRoleDTO> getRolesByUserId(Long userId);

    void updateUserRole(List<SysUserRoleDTO> sysUserRoles);

    void updateRoleMenu(List<SysRoleMenuDTO> sysRoleMenus);

//    void updateRolePermission(List<SysRoleMenu> sysRoleMenus);

//    List<SysMenu> queryMenusPermission();

    List<String> queryPermissionByUserId(Long userId);

    List<String> queryPermissionByRoleId(Long roleId);


    List<Long> queryMenuIdsByUserId(Long userId);

    List<Long> queryMenuIdsByRoleId(Long roleId);

    List<SysMenuDTO> queryMenusByRoleId(Long roleId);

//    List<Map<String, Object>> queryRolePermissions(SysRoleMenu record);
}
