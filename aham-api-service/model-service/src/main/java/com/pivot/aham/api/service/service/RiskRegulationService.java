package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.RiskRegulationPO;
import com.pivot.aham.common.core.base.BaseService;

public interface RiskRegulationService extends BaseService<RiskRegulationPO> {

    RiskRegulationPO queryByPO(RiskRegulationPO queryPO);
}
