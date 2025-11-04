package com.pivot.aham.admin.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.admin.server.BaseRemoteAdminServiceImpl;
import com.pivot.aham.admin.server.dto.SysSessionDTO;
import com.pivot.aham.admin.server.remoteservice.SysSessionRemoteService;
import com.pivot.aham.admin.service.mapper.model.SysSession;
import com.pivot.aham.admin.service.service.SysSessionService;
import com.pivot.aham.common.core.util.BeanMapperUtils;

import java.util.List;


@Service(interfaceClass = SysSessionRemoteService.class)
public class SysSessionRemoteServiceImpl extends BaseRemoteAdminServiceImpl<SysSession, SysSessionDTO, SysSessionService> implements SysSessionRemoteService {


    @Override
    public void deleteBySessionId(SysSessionDTO sysSessionDTO) {
        SysSession sysSession = BeanMapperUtils.map(sysSessionDTO,SysSession.class);
        service.deleteBySessionId(sysSession);
    }

    @Override
    public List<String> querySessionIdByAccount(SysSessionDTO sysSessionDTO) {
        SysSession sysSession = BeanMapperUtils.map(sysSessionDTO,SysSession.class);
        return service.querySessionIdByAccount(sysSession);
    }

    @Override
    public void cleanExpiredSessions() {
        service.cleanExpiredSessions();
    }
}
