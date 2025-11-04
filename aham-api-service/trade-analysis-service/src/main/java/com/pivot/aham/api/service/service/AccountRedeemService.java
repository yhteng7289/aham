package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.common.core.base.BaseService;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface AccountRedeemService  extends BaseService<AccountRedeemPO> {

    List<AccountRedeemPO> getRedeemListByTime(AccountRedeemPO accountRedeemPO);

    void updateByAccountId(AccountRedeemPO accountRedeemPO);

    void updateByTotalTmpOrderId(AccountRedeemPO accountRedeemPO);

    BigDecimal getAccountRedeem(List<AccountRedeemPO> accountRedeemPOs);

    List<AccountRedeemPO> listAccountRedeem(AccountRedeemPO redeemParam);

    void insertAccountRedeem(AccountRedeemPO accountRedeemPO);

    void updateById(AccountRedeemPO accountRedeemPO);

    void updateAccountRedeemToTncf(List<Long> accountRedeemIds, TncfStatusEnum tncfStatus);

    BigDecimal totalHasRedeemMoney(Long accountId, String clientId, String goalId);
    
    List<AccountRedeemPO> getRedeemListByTimeOrderPF(AccountRedeemPO accountRedeemPO); // Added By WooiTatt
    
    BigDecimal getSumRedeemConfirmAmount();
    
    BigDecimal getSumRedeemConfirmAmtByGoalClient(AccountRedeemPO accountRedeemPO);

}
