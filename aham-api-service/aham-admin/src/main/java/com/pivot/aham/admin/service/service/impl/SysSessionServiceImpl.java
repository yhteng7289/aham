package com.pivot.aham.admin.service.service.impl;

import java.util.Date;
import java.util.List;

import com.pivot.aham.admin.service.service.SysSessionService;
import com.pivot.aham.admin.service.mapper.SysSessionMapper;
import com.pivot.aham.admin.service.mapper.model.SysSession;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.util.CacheUtil;


@CacheConfig(cacheNames = "sysSession")
@Service
public class SysSessionServiceImpl extends BaseServiceImpl<SysSession, SysSessionMapper> implements SysSessionService {

    @Override
    @CachePut
    @Transactional
    public SysSession updateOrInsert(SysSession record) {
        if (record != null && record.getId() == null) {
            record.setUpdateTime(new Date());
            Long id = mapper.queryBySessionId(record.getSessionId());
            if (id != null) {
                mapper.updateById(record);
            } else {
                record.setCreateTime(new Date());
                mapper.insert(record);
            }
        } else {
            mapper.updateById(record);
        }
        return record;
    }

    @Override
    public void deleteBySessionId(final SysSession sysSession) {
        if (sysSession != null) {
            mapper.deleteBySessionId(sysSession.getSessionId());
        }
    }

    @Override
    public List<String> querySessionIdByAccount(SysSession sysSession) {
        if (sysSession != null) {
            return mapper.querySessionIdByAccount(sysSession.getUserName());
        }
        return null;
    }

    //
    @Override
    public void cleanExpiredSessions() {
        List<SysSession> sessions = queryList(new SysSession());
        for (SysSession sysSession : sessions) {
            if (sysSession != null) {
                logger.info("检查SESSION : {}", sysSession.getSessionId());
                if (!CacheUtil.getCache().exists(Constants.REDIS_SHIRO_SESSION + sysSession.getSessionId())) {
                    logger.info("移除SESSION : {}", sysSession.getSessionId());
                    delete(sysSession.getId());
                }
            }
        }
    }
}
