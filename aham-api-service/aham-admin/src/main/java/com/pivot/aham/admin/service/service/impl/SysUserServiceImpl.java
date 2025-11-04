package com.pivot.aham.admin.service.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.base.Joiner;
import com.pivot.aham.admin.service.service.SysAuthorizeService;
import com.pivot.aham.admin.service.service.SysDeptService;
import com.pivot.aham.admin.service.service.SysUserService;
import com.pivot.aham.admin.service.mapper.SysUserMapper;
import com.pivot.aham.admin.service.mapper.SysUserRoleMapper;
import com.pivot.aham.admin.service.mapper.model.SysDept;
import com.pivot.aham.admin.service.mapper.model.SysUserPO;
import com.pivot.aham.admin.service.mapper.model.SysUserRole;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月08日
 */
@CacheConfig(cacheNames = "sysUser")
@Service
public class SysUserServiceImpl extends BaseServiceImpl<SysUserPO, SysUserMapper> implements SysUserService {
    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysAuthorizeService sysAuthorizeService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    @Transactional
    public SysUserPO updateOrInsert(SysUserPO record) {

        //修改密码需校验密码
        if (DataUtil.isNotEmpty(record.getOldPassword())) {
            SysUserPO sysUser = super.queryById(record.getId());
            String encryptPassword = DigestUtil.md5Hex(record.getOldPassword());
            if (!sysUser.getPassword().equals(encryptPassword)) {
                throw new BusinessException("原密码错误");
            }
        }

        //修改密码
        if (DataUtil.isEmpty(record.getPassword())) {
            record.setPassword(null);
        } else {
            record.setPassword(DigestUtil.md5Hex(record.getPassword()));
        }
        return super.updateOrInsert(record);
    }

    @Override
    public SysUserPO queryById(Long id) {
        SysUserPO record = super.queryById(id);
        //填充所属部门
        fillDept(record);
        return record;
    }

    /**
     * 填充部门
     * @param record
     */
    private void fillDept(SysUserPO record) {
        if (record.getDeptId() != null) {
            SysDept sysDept = sysDeptService.queryById(record.getDeptId());
            if (sysDept != null) {
                record.setDeptName(sysDept.getDeptName());
            } else {
                record.setDeptId(null);
            }
        }
    }

    @Override
    public Page<SysUserPO> queryPageList(SysUserPO params, Page<SysUserPO> rowBounds) {
        Page<SysUserPO> pageInfo = super.queryPageList(params,rowBounds);
        for (SysUserPO userBean : pageInfo.getRecords()) {
            //填充所属部门
            fillDept(userBean);
//            //填充用户类别
//            if (userBean.getUserType() != null) {
//                userBean.setUserTypeText(userBean.getUserType().getDesc());
//            }

            //填充用户权限
            List<String> permissions = sysAuthorizeService.queryPermissionByUserId(userBean.getId());
            userBean.setPermissionCode(Joiner.on(";").join(permissions));

        }
        return pageInfo;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        super.delete(id);
        //删除用户角色关系
        sysUserRoleMapper.delete(new EntityWrapper<>(new SysUserRole().setUserId(id)));
    }

    @Override
    public void init() {
        queryList(new SysUserPO());
    }
}
