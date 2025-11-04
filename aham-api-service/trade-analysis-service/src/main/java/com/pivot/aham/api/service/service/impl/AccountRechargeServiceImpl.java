package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountRechargeMapper;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TpcfStatusEnum;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Service
public class AccountRechargeServiceImpl extends BaseServiceImpl<AccountRechargePO, AccountRechargeMapper> implements AccountRechargeService {
    @Override
    public void saveAccountRecharge(AccountRechargePO accountRecharge) {
        mapper.saveAccountRecharge(accountRecharge);
    }

    @Override
    public AccountRechargePO queryAccountRecharge(AccountRechargePO accountRechargePO) {
        return mapper.queryAccountRecharge(accountRechargePO);
    }

    @Override
    public void updateAccountRecharge(AccountRechargePO accountRechargePO) {
        mapper.updateAccountRecharge(accountRechargePO);
    }

    @Override
    public AccountRechargePO handleAccountRecharge(SaxoAccountOrderPO saxoAccountOrder, Date tradeTime) {
        AccountRechargePO accountRechargePO = new AccountRechargePO();
        accountRechargePO.setAccountId(saxoAccountOrder.getAccountId());
        accountRechargePO.setClientId(saxoAccountOrder.getClientId());
        accountRechargePO.setCurrency(saxoAccountOrder.getCurrency());
        accountRechargePO.setRechargeAmount(saxoAccountOrder.getCashAmount());
        accountRechargePO.setRechargeTime(tradeTime);
        accountRechargePO.setCreateTime(DateUtils.now());
        accountRechargePO.setUpdateTime(DateUtils.now());
        accountRechargePO.setOrderStatus(RechargeOrderStatusEnum.SUCCESS);
        accountRechargePO.setRechargeOrderNo(Sequence.next());
        accountRechargePO.setExecuteOrderNo(saxoAccountOrder.getExchangeOrderNo());
        accountRechargePO.setTpcfStatus(TpcfStatusEnum.PROCESSING);
//        accountRechargePO.setOperatType(RechargeOperatTypeEnum.UN_INVEST);
        accountRechargePO.setGoalId(saxoAccountOrder.getGoalId());
        return accountRechargePO;
    }

    @Override
    public List<AccountRechargePO> listByAccountId(AccountRechargePO po) {
        return mapper.listByAccountId(po);
    }

    @Override
    public List<AccountRechargePO> listAccountRecharge(AccountRechargePO accountRecharge) {
        return mapper.listAccountRecharge(accountRecharge);
    }
    
    @Override
    public BigDecimal getSumAccountRecharge() {
        BigDecimal totalAccountRecharge = BigDecimal.ZERO;
        try{
            totalAccountRecharge = mapper.getSumAccountRecharge();
        }catch(Exception e){}
        return totalAccountRecharge;
    }
    
    @Override
    public BigDecimal getSumAccRechargeByGoalClient(AccountRechargePO accountRechargePO) {
        BigDecimal totalAccountRecharge = BigDecimal.ZERO;
        try{
            totalAccountRecharge = mapper.getSumAccRechargeByGoalClient(accountRechargePO);
        }catch(Exception e){}
        return totalAccountRecharge;
    }

}
