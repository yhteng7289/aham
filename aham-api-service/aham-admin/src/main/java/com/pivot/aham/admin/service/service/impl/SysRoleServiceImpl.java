package com.pivot.aham.admin.service.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.pivot.aham.admin.service.service.SysDeptService;
import com.pivot.aham.admin.service.mapper.SysRoleMapper;
import com.pivot.aham.admin.service.mapper.SysRoleMenuMapper;
import com.pivot.aham.admin.service.mapper.SysUserRoleMapper;
import com.pivot.aham.admin.service.mapper.model.SysDept;
import com.pivot.aham.admin.service.mapper.model.SysRole;
import com.pivot.aham.admin.service.mapper.model.SysRoleMenu;
import com.pivot.aham.admin.service.mapper.model.SysUserRole;
import com.pivot.aham.admin.service.service.SysRoleService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@CacheConfig(cacheNames = "sysRole")
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRole, SysRoleMapper> implements SysRoleService {
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public SysRole queryById(Long id) {
        SysRole sysRole = super.queryById(id);
        //填充部门名称
        if (sysRole != null) {
            if (sysRole.getDeptId() != null && sysRole.getDeptId() != 0) {
                SysDept sysDept = sysDeptService.queryById(sysRole.getDeptId());
                if (sysDept != null) {
                    sysRole.setDeptName(sysDept.getDeptName());
                } else {
                    sysRole.setDeptId(null);
                }
            }
        }
        return sysRole;
    }

//    @Override
//    public Pagination<SysRole> query(Map<String, Object> params) {
//        Pagination<SysRole> pageInfo = super.query(params);
//        //填充权限和部门
//        for (SysRole wrapperbean : pageInfo.getRecords()) {
//            if (wrapperbean.getDeptId() != null && wrapperbean.getDeptId() != 0) {
//                SysDept sysDept = sysDeptService.queryById(wrapperbean.getDeptId());
//                if (sysDept != null) {
//                    wrapperbean.setDeptName(sysDept.getDeptName());
//                }
//            }
//            List<String> permissions = sysAuthorizeService.queryRolePermission(wrapperbean.getId());
//            wrapperbean.setPermission(Joiner.on(";").join(permissions));
//        }
//        return pageInfo;
//    }

    @Override
    @Transactional
    public void delete(Long id) {
        super.delete(id);
        //删除用户角色关系
        sysUserRoleMapper.delete(new EntityWrapper<>(new SysUserRole().setRoleId(id)));
        //删除角色权限关系
        sysRoleMenuMapper.delete(new EntityWrapper<>(new SysRoleMenu().setPermissionId(id)));
    }
}
