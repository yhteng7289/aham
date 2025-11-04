package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.BaseRemoteAdminServiceImpl;
import com.pivot.aham.admin.server.dto.SysRoleDTO;
import com.pivot.aham.admin.server.remoteservice.SysRoleRemoteService;
import com.pivot.aham.admin.service.mapper.model.SysRole;
import com.pivot.aham.admin.service.service.SysRoleService;


@Service(interfaceClass = SysRoleRemoteService.class)
public class SysRoleRemoteServiceImpl extends BaseRemoteAdminServiceImpl<SysRole, SysRoleDTO,SysRoleService> implements SysRoleRemoteService {

}
