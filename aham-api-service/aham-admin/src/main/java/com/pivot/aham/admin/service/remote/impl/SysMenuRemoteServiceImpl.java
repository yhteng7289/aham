package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.BaseRemoteAdminServiceImpl;
import com.pivot.aham.admin.server.dto.SysMenuDTO;
import com.pivot.aham.admin.server.remoteservice.SysMenuRemoteService;
import com.pivot.aham.admin.service.mapper.model.SysMenu;
import com.pivot.aham.admin.service.service.SysMenuService;


@Service(interfaceClass = SysMenuRemoteService.class)
public class SysMenuRemoteServiceImpl extends BaseRemoteAdminServiceImpl<SysMenu, SysMenuDTO, SysMenuService> implements SysMenuRemoteService {

}
