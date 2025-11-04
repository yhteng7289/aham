package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountBalanceAdjDetailMapper;
import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.api.service.service.AccountBalanceAdjDetailService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author addison
 */
@Service
public class AccountBalanceAdjDetailServiceImpl extends BaseServiceImpl<AccountBalanceAdjDetail, AccountBalanceAdjDetailMapper> implements AccountBalanceAdjDetailService {

    @Override
    public void batchInsertBalanceAdjDetail(List<AccountBalanceAdjDetail> accountBalanceAdjDetailList) {
        mapper.batchInsertBalanceAdjDetail(accountBalanceAdjDetailList);
    }
}
