package com.pivot.aham.admin.service.service;

import java.util.List;

import com.pivot.aham.admin.service.mapper.model.SysSession;
import com.pivot.aham.common.core.base.BaseService;


public interface SysSessionService extends BaseService<SysSession> {
    void deleteBySessionId(final SysSession sysSession);

    List<String> querySessionIdByAccount(SysSession sysSession);

    void cleanExpiredSessions();
}
