package com.pivot.aham.api.service.service;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.common.core.base.BaseService;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface AccountRechargeService extends BaseService<AccountRechargePO>{

    void saveAccountRecharge(AccountRechargePO accountRecharge);

    AccountRechargePO queryAccountRecharge(AccountRechargePO accountRechargePO);

    void updateAccountRecharge(AccountRechargePO accountRechargePO);

    /**
     * 充值完成记录充值流水
     *
     * @return
     * @param processingSgd
     * @param tradeTime
     */
    AccountRechargePO handleAccountRecharge(SaxoAccountOrderPO processingSgd, Date tradeTime);

    List<AccountRechargePO> listByAccountId(AccountRechargePO po);

    List<AccountRechargePO> listAccountRecharge(AccountRechargePO accountRecharge);
    
    BigDecimal getSumAccountRecharge();
    
    BigDecimal getSumAccRechargeByGoalClient(AccountRechargePO accountRechargePO);


}
