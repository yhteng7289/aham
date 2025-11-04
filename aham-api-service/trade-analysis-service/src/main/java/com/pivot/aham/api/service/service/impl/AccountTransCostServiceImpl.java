package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountTransCostMapper;
import com.pivot.aham.api.service.mapper.model.AccountTransCost;
import com.pivot.aham.api.service.service.AccountTransCostService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountTransCostServiceImpl extends BaseServiceImpl<AccountTransCost, AccountTransCostMapper> implements AccountTransCostService {

    @Override
    public List<AccountTransCost> selectByDay(AccountTransCost accountTransCost) {
        return mapper.selectByDay(accountTransCost);
    }
}
