package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.SysConfigMapper;
import com.pivot.aham.api.service.mapper.model.SysConfigPO;
import com.pivot.aham.api.service.service.SysConfigService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author addison
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfigPO, SysConfigMapper> implements SysConfigService {

    @Override
    public SysConfigPO getStatus(String configName) {
        return mapper.getStatus(configName);
    }

}
