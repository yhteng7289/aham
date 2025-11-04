package com.pivot.aham.api.service.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.AccountDividendMapper;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.service.AccountDividendService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class AccountDividendServiceImpl extends BaseServiceImpl<AccountDividendPO, AccountDividendMapper> implements AccountDividendService {

    @Override
    public AccountDividendPO queryAccountDividend(AccountDividendPO accountDividend) {
        return mapper.queryAccountDividend(accountDividend);
    }

    @Override
    public void insert(AccountDividendPO accountDividendPO) {
        mapper.saveAccountDividend(accountDividendPO);
    }

    @Override
    public void updateAccountDividend(AccountDividendPO accountDividendPO) {
        mapper.updateAccountDividend(accountDividendPO);
    }

    @Override
    public BigDecimal getAccountDividendMoney(AccountDividendPO accountDividendParam) {
        List<AccountDividendPO> accountDividendPOS = mapper.listAccountDividend(accountDividendParam);
        BigDecimal accountDividend = BigDecimal.ZERO;
        for (AccountDividendPO accountDividendPO : accountDividendPOS) {
            accountDividend = accountDividend.add(accountDividendPO.getNavDividendAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return accountDividend;
    }

    @Override
    public List<AccountDividendPO> listAccountDividend(AccountDividendPO accountDividendParam) {
        return mapper.listAccountDividend(accountDividendParam);
    }

    @Override
    public Page<AccountDividendPO> listAccountDividendPage(Page<AccountDividendPO> rowBounds, AccountDividendPO accountDividendParam) {
        List<AccountDividendPO> accountDividendPOS = mapper.listAccountDividendPage(rowBounds,accountDividendParam);
        rowBounds.setRecords(accountDividendPOS);
        return rowBounds;

    }


}
