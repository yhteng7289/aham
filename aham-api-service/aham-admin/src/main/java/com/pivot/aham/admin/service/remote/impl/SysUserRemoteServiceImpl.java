package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.BaseRemoteAdminServiceImpl;
import com.pivot.aham.admin.server.dto.SysUserDTO;
import com.pivot.aham.admin.server.remoteservice.SysUserRemoteService;
import com.pivot.aham.admin.service.mapper.model.SysUserPO;
import com.pivot.aham.admin.service.service.SysUserService;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月08日
 */
@Service(interfaceClass = SysUserRemoteService.class)
public class SysUserRemoteServiceImpl extends BaseRemoteAdminServiceImpl<SysUserPO, SysUserDTO, SysUserService> implements SysUserRemoteService {

    @Override
    public void init() {
        service.init();
    }
}
