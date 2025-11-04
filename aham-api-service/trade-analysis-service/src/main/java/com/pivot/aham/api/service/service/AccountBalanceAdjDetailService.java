package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface AccountBalanceAdjDetailService extends BaseService<AccountBalanceAdjDetail> {
    void batchInsertBalanceAdjDetail(List<AccountBalanceAdjDetail> accountBalanceAdjDetailList);
}
