package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.RiskRegulationMapper;
import com.pivot.aham.api.service.mapper.model.RiskRegulationPO;
import com.pivot.aham.api.service.service.RiskRegulationService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "riskRegulation")
@Service
public class RiskRegulationServiceImpl extends BaseServiceImpl<RiskRegulationPO, RiskRegulationMapper> implements RiskRegulationService {
    @Override
    public RiskRegulationPO queryByPO(RiskRegulationPO queryPO) {
        return mapper.queryByPO(queryPO);
    }
}
