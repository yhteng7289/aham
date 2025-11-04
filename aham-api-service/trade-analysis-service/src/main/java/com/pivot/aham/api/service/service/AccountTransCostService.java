package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountTransCost;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface AccountTransCostService extends BaseService<AccountTransCost> {
    List<AccountTransCost> selectByDay(AccountTransCost accountTransCost);
}
