package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.api.service.mapper.model.FeesConfigPO;
import com.pivot.aham.common.core.base.BaseService;


public interface FeesConfigService extends BaseService<FeesConfigPO> {
    FeesConfigPO selectByDay(FeesConfigPO feesConfigPO);
}
