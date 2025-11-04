package com.pivot.aham.admin.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.pivot.aham.admin.service.service.SysAuthorizeService;
import com.pivot.aham.admin.service.mapper.SysAuthorizeMapper;
import com.pivot.aham.admin.service.mapper.SysRoleMenuMapper;
import com.pivot.aham.admin.service.mapper.SysUserRoleMapper;
import com.pivot.aham.admin.service.mapper.model.SysMenu;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import com.pivot.aham.admin.service.mapper.model.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@CacheConfig(cacheNames = "sysAuthorize")
@Service
public class SysAuthorizeServiceImpl implements SysAuthorizeService {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Autowired
    private SysAuthorizeMapper sysAuthorizeMapper;

    @Override
    public List<SysUserRole> getRolesByUserId(Long userId) {
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(userId);
        Wrapper<SysUserRole> wrapper = new EntityWrapper<>(sysUserRole);
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(wrapper);

        return userRoles;
    }

    @Override
    @Transactional
//    @CacheEvict(value = {Constants.CACHE_NAMESPACE + "menuPermission", Constants.CACHE_NAMESPACE + "sysPermission",
//    Constants.CACHE_NAMESPACE + "userPermission", Constants.CACHE_NAMESPACE + "rolePermission"}, allEntries = true)
    public void updateUserRole(List<SysUserRole> sysUserRoles) {
        Long userId = null;
        for (SysUserRole sysUserRole : sysUserRoles) {
            if (sysUserRole != null && sysUserRole.getUserId() != null) {
                userId = sysUserRole.getUserId();
                break;
            }
        }
        if (userId != null) {
            sysAuthorizeMapper.deleteUserRole(userId);
        }
        for (SysUserRole sysUserRole : sysUserRoles) {
            if (sysUserRole != null && sysUserRole.getUserId() != null && sysUserRole.getRoleId() != null) {
                sysUserRoleMapper.insert(sysUserRole);
            }
        }
    }



    @Override
    @Transactional
//    @CacheEvict(value = {Constants.CACHE_NAMESPACE + "menuPermission", Constants.CACHE_NAMESPACE + "sysPermission",
//    Constants.CACHE_NAMESPACE + "userPermission", Constants.CACHE_NAMESPACE + "rolePermission"}, allEntries = true)
    public void updateRoleMenu(List<SysRoleMenu> sysRoleMenus) {
        Long roleId = null;
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            roleId = sysRoleMenu.getRoleId();
            break;
        }
        if (roleId != null) {
            sysAuthorizeMapper.deleteRoleMenu(roleId);
        }
        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }

//    @Override
//    @Transactional
//    @CacheEvict(value = {Constants.CACHE_NAMESPACE + "menuPermission", Constants.CACHE_NAMESPACE + "sysPermission",
//    Constants.CACHE_NAMESPACE + "userPermission", Constants.CACHE_NAMESPACE + "rolePermission"}, allEntries = true)
//    public void updateRolePermission(List<SysRoleMenu> sysRoleMenus) {
//        Long roleId = null;
//        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
//            if (sysRoleMenu != null && sysRoleMenu.getRoleId() != null) {
//                roleId = sysRoleMenu.getRoleId();
//            }
//        }
//        if (roleId != null) {
//            sysAuthorizeMapper.deleteRoleMenu(roleId);
//        }
//        for (SysRoleMenu sysRoleMenu : sysRoleMenus) {
//            sysRoleMenuMapper.insert(sysRoleMenu);
//        }
//    }

    @Override
//    @Cacheable(Constants.CACHE_NAMESPACE + "sysPermission")
    public List<String> queryPermissionByUserId(Long userId) {
        return sysAuthorizeMapper.queryPermissionByUserId(userId);
    }

    @Override
    public List<String> queryPermissionByRoleId(Long roleId) {
        return sysAuthorizeMapper.queryPermissionByRoleId(roleId);
    }

    @Override
    public List<Long> queryMenuIdsByUserId(Long userId) {
        return sysAuthorizeMapper.queryMenuIdsByUserId(userId);
    }

    @Override
    public List<Long> queryMenuIdsByRoleId(Long roleId) {
        return sysRoleMenuMapper.queryMenuIdsByRoleId(roleId);
    }

    @Override
    public List<SysMenu> queryMenusByRoleId(Long roleId) {
        return sysAuthorizeMapper.queryMenusByRoleId(roleId);
    }

//    @Override
//    @Cacheable(Constants.CACHE_NAMESPACE + "rolePermission")
//    public List<String> queryRolePermission(Long roleId) {
//        return sysRoleMenuMapper.queryPermission(roleId);
//    }

//    @Override
//    public List<SysMenu> queryMenusPermission() {
//        return sysAuthorizeMapper.queryMenusPermission();
//    }

//    @Override
//    public List<Map<String, Object>> queryRolePermissions(SysRoleMenu sysRoleMenu) {
//        List<Map<String, Object>> list = sysRoleMenuMapper.queryPermissions(sysRoleMenu.getRoleId());
//        return list;
//    }
}
