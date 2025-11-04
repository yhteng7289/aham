package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.common.core.base.BaseService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
public interface AccountDividendService extends BaseService<AccountDividendPO> {

    AccountDividendPO queryAccountDividend(AccountDividendPO accountDividendParam);

    void insert(AccountDividendPO accountDividendPO);

    void updateAccountDividend(AccountDividendPO accountDividendPO);

    /**
     * 获取该Account上的为处理的分红
     *
     * @param accountDividendPO
     * @return
     */
    BigDecimal getAccountDividendMoney(AccountDividendPO accountDividendPO);

    List<AccountDividendPO> listAccountDividend(AccountDividendPO accountDividendParam);

    Page<AccountDividendPO> listAccountDividendPage(Page<AccountDividendPO> rowBounds, AccountDividendPO accountDividendParam);

}
