package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountNormalFeeMapper;
import com.pivot.aham.api.service.mapper.FeesConfigMapper;
import com.pivot.aham.api.service.mapper.model.AccountNormalFee;
import com.pivot.aham.api.service.mapper.model.FeesConfigPO;
import com.pivot.aham.api.service.service.AccountNormalFeeService;
import com.pivot.aham.api.service.service.FeesConfigService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeesConfigServiceImpl extends BaseServiceImpl<FeesConfigPO, FeesConfigMapper> implements FeesConfigService {

    @Override
    public FeesConfigPO selectByDay(FeesConfigPO feesConfigPO) {
        return mapper.selectByDay(feesConfigPO);
    }
}
