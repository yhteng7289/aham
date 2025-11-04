package com.pivot.aham.admin.service.mapper;

import java.util.List;

import com.pivot.aham.admin.service.mapper.model.SysSession;
import org.springframework.stereotype.Repository;
import com.pivot.aham.common.core.base.BaseMapper;

@Repository
public interface SysSessionMapper extends BaseMapper<SysSession> {

    void deleteBySessionId(String sessionId);

    Long queryBySessionId(String sessionId);

    List<String> querySessionIdByAccount(String account);
}