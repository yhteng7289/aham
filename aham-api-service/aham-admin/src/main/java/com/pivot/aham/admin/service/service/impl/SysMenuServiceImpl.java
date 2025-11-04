package com.pivot.aham.admin.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pivot.aham.admin.service.service.SysMenuService;
import com.pivot.aham.admin.service.mapper.SysMenuMapper;
import com.pivot.aham.admin.service.mapper.SysRoleMenuMapper;
import com.pivot.aham.admin.service.mapper.model.SysMenu;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@CacheConfig(cacheNames = "sysMenu")
@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenu, SysMenuMapper> implements SysMenuService {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public SysMenu queryById(Long id) {
        SysMenu sysMenu = super.queryById(id);
        if (sysMenu != null) {
            if (sysMenu.getParentId() != null && sysMenu.getParentId() != 0) {
                SysMenu parent = super.queryById(sysMenu.getParentId());
                if (parent != null) {
                    sysMenu.setParentName(parent.getPermissionName());
                } else {
                    sysMenu.setParentId(null);
                }
            }
        }
        return sysMenu;
    }
    @Override
    @Transactional
    public void delete(Long id) {
        super.delete(id);
        sysRoleMenuMapper.delete(new EntityWrapper<>(new SysRoleMenu().setPermissionId(id)));
    }
}
