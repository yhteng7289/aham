package com.pivot.aham.admin.service.service;


import com.pivot.aham.admin.service.mapper.model.SysMenu;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import com.pivot.aham.admin.service.mapper.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限认证
 * @author addison
 * @since 2018年11月11日 上午10:59:37
 */
public interface SysAuthorizeService {
    List<SysUserRole> getRolesByUserId(Long userId);

    void updateUserRole(List<SysUserRole> sysUserRoles);

    void updateRoleMenu(List<SysRoleMenu> sysRoleMenus);

//    void updateRolePermission(List<SysRoleMenu> sysRoleMenus);

//    List<SysMenu> queryMenusPermission();

    List<String> queryPermissionByUserId(Long userId);

    List<String> queryPermissionByRoleId(Long roleId);


    List<Long> queryMenuIdsByUserId(Long userId);

    List<Long> queryMenuIdsByRoleId(Long roleId);
    List<SysMenu> queryMenusByRoleId(@Param("roleId") Long roleId);

//    List<Map<String, Object>> queryRolePermissions(SysRoleMenu record);
}
