package com.pivot.aham.admin.server.remoteservice;

import com.pivot.aham.admin.server.BaseAdminService;
import com.pivot.aham.admin.server.dto.SysSessionDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;

import java.util.List;


public interface SysSessionRemoteService extends BaseRemoteService, BaseAdminService<SysSessionDTO> {
    void deleteBySessionId(final SysSessionDTO sysSessionDTO);

    List<String> querySessionIdByAccount(SysSessionDTO sysSessionDTO);

    void cleanExpiredSessions();
}
