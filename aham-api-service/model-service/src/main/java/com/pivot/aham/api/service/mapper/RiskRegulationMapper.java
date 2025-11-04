package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.RiskRegulationPO;
import com.pivot.aham.common.core.base.BaseMapper;

public interface RiskRegulationMapper extends BaseMapper<RiskRegulationPO> {
    RiskRegulationPO queryByPO(RiskRegulationPO queryPO);
}
