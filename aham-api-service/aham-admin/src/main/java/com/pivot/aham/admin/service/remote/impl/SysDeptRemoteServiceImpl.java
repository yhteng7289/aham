package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.BaseRemoteAdminServiceImpl;
import com.pivot.aham.admin.server.dto.SysDeptDTO;
import com.pivot.aham.admin.server.remoteservice.SysDeptRemoteService;
import com.pivot.aham.admin.service.service.SysDeptService;
import com.pivot.aham.admin.service.mapper.model.SysDept;

@Service(interfaceClass = SysDeptRemoteService.class)
public class SysDeptRemoteServiceImpl extends BaseRemoteAdminServiceImpl<SysDept,SysDeptDTO, SysDeptService> implements SysDeptRemoteService {

}
