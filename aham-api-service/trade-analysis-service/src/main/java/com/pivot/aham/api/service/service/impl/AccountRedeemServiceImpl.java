package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.AccountRedeemMapper;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.enums.analysis.RedeemOrderStatusEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Service
public class AccountRedeemServiceImpl extends BaseServiceImpl<AccountRedeemPO, AccountRedeemMapper> implements AccountRedeemService {

    @Override
    public List<AccountRedeemPO> getRedeemListByTime(AccountRedeemPO accountRedeemPO) {
        return mapper.getRedeemListByTime(accountRedeemPO);
    }

    @Override
    public void updateByAccountId(AccountRedeemPO accountRedeemPO) {

        mapper.updateByAccountId(accountRedeemPO);
    }

    @Override
    public void updateByTotalTmpOrderId(AccountRedeemPO accountRedeemPO) {

        mapper.updateByTotalTmpOrderId(accountRedeemPO);
    }

    @Override
    public BigDecimal getAccountRedeem(List<AccountRedeemPO> accountRedeemPOs) {
        BigDecimal applyMoney = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOs) {
            if (accountRedeemPO.getApplyMoney().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            applyMoney = applyMoney.add(accountRedeemPO.getApplyMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return applyMoney;
    }

    @Override
    public List<AccountRedeemPO> listAccountRedeem(AccountRedeemPO accountRedeem) {
        return mapper.listAccountRedeem(accountRedeem);
    }

    @Override
    public void insertAccountRedeem(AccountRedeemPO accountRedeemPO) {
        mapper.insertAccountRedeem(accountRedeemPO);
    }

    @Override
    public void updateById(AccountRedeemPO accountRedeemPO) {
        mapper.updateAccountRedeemById(accountRedeemPO);
    }

    @Override
    public void updateAccountRedeemToTncf(List<Long> accountRedeemIds, TncfStatusEnum tncfStatus) {
        mapper.updateAccountRedeemToTncf(accountRedeemIds, tncfStatus);
    }

    @Override
    public BigDecimal totalHasRedeemMoney(Long accountId, String clientId, String goalId) {
        AccountRedeemPO accountRedeemParam = new AccountRedeemPO();
        accountRedeemParam.setAccountId(accountId);
        accountRedeemParam.setClientId(clientId);
        accountRedeemParam.setOrderStatus(RedeemOrderStatusEnum.PROCESSING);
        accountRedeemParam.setGoalId(goalId);
        List<AccountRedeemPO> accountRedeemPOS = mapper.listAccountRedeem(accountRedeemParam);
        BigDecimal totalHasRedeem = BigDecimal.ZERO;
        for (AccountRedeemPO accountRedeemPO : accountRedeemPOS) {
            totalHasRedeem = totalHasRedeem.add(accountRedeemPO.getApplyMoney()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return totalHasRedeem;
    }
    
    @Override
    public List<AccountRedeemPO> getRedeemListByTimeOrderPF(AccountRedeemPO accountRedeemPO) {
        return mapper.getRedeemListByTimeOrderPF(accountRedeemPO);
    }
    
    @Override
    public BigDecimal getSumRedeemConfirmAmount() {
        BigDecimal totalAccountRedeem = BigDecimal.ZERO;
        try{
            totalAccountRedeem = mapper.getSumRedeemConfirmAmount();
        }catch(Exception e){}
        return totalAccountRedeem;
    }
    
    @Override
    public BigDecimal getSumRedeemConfirmAmtByGoalClient(AccountRedeemPO accountRedeemPO) {
        BigDecimal totalAccountRedeem = BigDecimal.ZERO;
        try{
            totalAccountRedeem = mapper.getSumRedeemConfirmAmtByGoalClient(accountRedeemPO);
        }catch(Exception e){}
        return totalAccountRedeem;
    }

}
